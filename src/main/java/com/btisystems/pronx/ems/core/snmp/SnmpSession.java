/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btisystems.pronx.ems.core.snmp;

import com.btisystems.pronx.ems.core.exception.SystemObjectIdException;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeListener;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * A {@link ISnmpSession}.
 */
public class SnmpSession extends DefaultPDUFactory implements ISnmpSession {

    /**
     * The constant SPACE.
     */
    public static final String SPACE = " ";
    /**
     * Main logger for SNMP session.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(SnmpSession.class);
    /**
     * Fine grained logger for var bind set requests.
     */
    protected static final Logger SETTER_LOG = LoggerFactory.getLogger(ISnmpSession.class.getName() + ".set");
    /**
     * Fine grained logger for walk requests.
     */
    protected static final Logger WALKER_LOG = LoggerFactory.getLogger(ISnmpSession.class.getName() + ".walk");
    private static final String SYSTEM_OBJECT_ID_OID = "1.3.6.1.2.1.1.2.0";
    private static final String NO_SUCH_OBJECT = "noSuchObject";
    private static final String NULL = "Null";
    private final ISnmpConfiguration snmpConfiguration;
    private final Target target;
    private final Address address;
    private final Session snmpInterface;

    private final ISnmpTableWalker tableWalker;

    /**
     * Class constructor.
     *
     * @param snmpConfiguration the configuration to be used for the session
     * @param snmp              session to be wrapped
     * @param target            snmp4j's remote SNMP entity reference
     * @param address           address of the device associated with the session
     */
    public SnmpSession(final ISnmpConfiguration snmpConfiguration,
                       final Session snmp,
                       final Target target,
                       final Address address) {
        this.snmpConfiguration = snmpConfiguration;
        this.snmpInterface = snmp;
        this.address = address;
        this.target = target;

        tableWalker = new SnmpTableWalker(snmpConfiguration, snmp, target, address);
    }

    @Override
    public String identifyDevice() {
        LOG.debug(">>> identifyDevice address:{}", getHostAddress());
        try {
            return getVariableValue(SYSTEM_OBJECT_ID_OID).toString();
        } catch (final IOException e) {
            throw new SystemObjectIdException(address, e);
        }
    }


    @Override
    public String getVariable(final String oid) {
        LOG.debug(">>> getVariable oid:{}, address:{}", oid, getHostAddress());
        try {
            return getVariableValue(oid).toString();
        } catch (final IOException e) {
            LOG.debug("Ignoring IOException {}", e.getMessage());
            LOG.trace("IOException {}", e);

        }
        return null;
    }

    @Override
    public Integer getVariableAsInt(final String oid) {
        LOG.debug(">>> getVariableAsInt oid:{}, address:{}", oid, getHostAddress());
        try {
            return getVariableValue(oid).toInt();
        } catch (final IOException e) {
            LOG.debug("Ignoring IOException {}", e.getMessage());
            LOG.trace("IOException {}", e);
        }
        return null;
    }


    @Override
    public WalkResponse walkDevice(final IVariableBindingHandler networkDevice,
                                   final List<OID> oids) {
        WALKER_LOG.debug(">>> walkDevice address:{}", getHostAddress());
        WALKER_LOG.trace(">>> walkDevice address:{} for oids={}", getHostAddress(), oids);

        final TreeUtils treeUtils = new TreeUtils(snmpInterface, this);
        final TreeResponseListener treeListener = new TreeResponseListener(networkDevice, oids, treeUtils);


        synchronized (treeListener) {

            treeUtils.setIgnoreLexicographicOrder(true);
            treeUtils.setMaxRepetitions(snmpConfiguration.getMaxRepetitions());
            WALKER_LOG.debug("NE:{},  First subtree:{}", getHostAddress(), oids.get(0));
            treeUtils.getSubtree(target, oids.get(0), this, treeListener);
            try {
                // Allow for possibility that listener has already terminated in this thread,
                // which it might have done if the initial Snmp.send threw an IOException
                if (!treeListener.isFinished()) {
                    final long startTime = System.currentTimeMillis();
                    while ((!treeListener.isFinished()) && ((System.currentTimeMillis() - startTime) < snmpConfiguration.getWalkTimeout())) {
                        treeListener.wait(snmpConfiguration.getWalkTimeout());
                    }
                    //Timed out rather than finished.
                    if (!treeListener.isFinished()) {
                        treeListener.stopWalk();
                        LOG.error("Walk for device {} timed out.", getHostAddress());
                        return new WalkResponse(new WalkException("Walk timed out"));
                    }
                }
            } catch (final InterruptedException ex) {
                Thread.interrupted();
                WALKER_LOG.warn("listener wait interrupted:{}", ex);
            }
            return treeListener.getResponse();
        }
    }

    @Override
    public WalkResponse getTableRows(final IVariableBindingHandler networkDevice,
                                     final Map<DeviceEntityDescription, List<OID>> tableIndexes) throws IOException {

        return getTableWalker().getTableRows(networkDevice, tableIndexes);
    }

    @Override
    public InetAddress getAddress() {
        return ((IpAddress) address).getInetAddress();
    }

    @Override
    public void close() throws IOException {
        // Actually nothing to do.
        // snmpInterface is a singleton and should not be closed.
    }

    @Override
    public void setVariables(final VariableBinding[] bindings) {
        final String hostIp = getHostAddress();
        SETTER_LOG.debug(">>> setVariable bindings:{}, address {}", bindings, hostIp);


        final PDU request = snmpConfiguration.createPDU(PDU.SET);
        request.addAll(bindings);

        final ResponseEvent responseEvent = send(request, hostIp);
        processResponseEvent(responseEvent, hostIp);
    }

    private ResponseEvent send(final PDU request, final String hostIp) throws SnmpIoException {
        ResponseEvent responseEvent;
        try {
            responseEvent = snmpInterface.send(request, target);
        } catch (final IOException e) {
            LOG.debug("Failed to set variables:", e);
            SETTER_LOG.warn("IO Exception sending to host {}, message={}", hostIp, e.getMessage());
            throw new SnmpIoException(hostIp, e.getMessage());
        }
        return responseEvent;
    }

    private void processResponseEvent(final ResponseEvent responseEvent, final String hostIp) throws SnmpIoException {
        if (responseEvent.getError() != null) {
            SETTER_LOG.warn("Response when sending to host {} has error: {}", hostIp, responseEvent.getError().getMessage());
            throw new SnmpIoException(hostIp, responseEvent.getError().getMessage());
        }

        final PDU response = responseEvent.getResponse();

        if (response != null) {
            final int errorStatusId = response.getErrorStatus();
            if (errorStatusId != SnmpConstants.SNMP_ERROR_SUCCESS) {
                SETTER_LOG.warn("Response PDU when sending to host {} has error: {}", hostIp, constructErrorMessage(response));

                checkErrorCodeAndDescription();

                if (SnmpConstants.SNMP_ERROR_AUTHORIZATION_ERROR == errorStatusId) {
                    final String authErrorMsg = "Authorization error occurred; Please confirm that the device is running normally and verify the SNMP community strings.";
                    throw new SnmpIoException(hostIp, authErrorMsg);
                }

                throw new SnmpIoException(hostIp, response.getErrorStatusText());
            }
            SETTER_LOG.debug("Success");
        } else {
            SETTER_LOG.debug("Timed Out");
            throw new SnmpIoException(hostIp, "Timed out");
        }
    }

    private static String constructErrorMessage(final PDU response) {
        final int bindingIndex = response.getErrorIndex() - 1;
        Object error = "";
        if (bindingIndex > -1) {
            error = response.getVariableBindings().get(bindingIndex);
        }
        return response.getErrorStatusText() + SPACE + error;
    }

    @Override
    public void checkErrorCodeAndDescription() {
        //Empty implementation, i.e. no error code handling.
    }

    /**
     * Gets host address.
     *
     * @return the host address
     */
    protected String getHostAddress() {
        return ((IpAddress) address).getInetAddress().getHostAddress();
    }

    // Get the value of a variable specified by its oid.
    private Variable getVariableValue(final String oid) throws IOException {
        LOG.debug(">>> getVariableValue oid:{}", oid);

        PDU response;
        Variable var;

        final PDU request = snmpConfiguration.createPDU(PDU.GET);
        request.add(new VariableBinding(new OID(oid)));

        ResponseEvent responseEvent;
        responseEvent = snmpInterface.send(request, target);
        if (responseEvent != null && responseEvent.getResponse() != null) {
            response = responseEvent.getResponse();
            var = response.get(0).getVariable();
        } else {
            throw new IOException("No response from device. "
                    + "Please confirm that the device is running normally and verify the SNMP community strings.");
        }
        return var;
    }

    @Override
    public PDU createPDU(final Target target) {
        final PDU request = snmpConfiguration.createPDU(PDU.GETBULK);
        return request;
    }

    /**
     * Is invalid boolean.
     *
     * @param value the value
     * @return the boolean
     */
    protected boolean isInvalid(final String value) {
        return value == null || value.equals(NULL) || value.equals(NO_SUCH_OBJECT);
    }

    protected ISnmpTableWalker getTableWalker() {
        return tableWalker;
    }

    private class TreeResponseListener implements TreeListener {

        private final long startTime = System.currentTimeMillis();
        private final IVariableBindingHandler networkDevice;
        private final List<OID> oids;
        private final TreeUtils treeUtils;
        private boolean finished;
        private int requests;
        private int objects;
        private WalkResponse response;
        private int oidIndex;
        private OID lastProcessedOid;

        /**
         * Instantiates a new Tree response listener.
         *
         * @param networkDevice the network device
         * @param oids          the oids
         * @param treeUtils     the tree utils
         */
        public TreeResponseListener(final IVariableBindingHandler networkDevice,
                                    final List<OID> oids,
                                    final TreeUtils treeUtils) {
            this.networkDevice = networkDevice;
            this.oids = oids;
            this.treeUtils = treeUtils;
            oidIndex = 0;
            finished = false;
        }

        /**
         * Gets response.
         *
         * @return the response
         */
        public WalkResponse getResponse() {
            return (response == null) ? new WalkResponse(new WalkException("Walk interrupted")) : response;
        }

        /**
         * Stop walk.
         */
        public void stopWalk() {
            finished = true;
        }        @Override
        public boolean next(final TreeEvent e) {
            requests++;
            final VariableBinding[] vbs = e.getVariableBindings();
            for (int i = 0; i < vbs.length; i++) {
                if (addVariable(vbs, i)) {
                    objects++;
                }
            }

            return !finished;
        }

        private boolean addVariable(final VariableBinding[] vbs, final int i) {
            try {
                final VariableBinding binding = vbs[i];
                final boolean wasAdded = networkDevice.addVariable(binding);
                if (wasAdded) {
                    WALKER_LOG.debug("Element {} added varbind: {}.", getHostAddress(), binding);
                    lastProcessedOid = binding.getOid();
                } else {
                    LOG.debug("Element {} unknown varbind: {}", getHostAddress(), binding);
                    return false;
                }
            } catch (final RuntimeException e) {
                WALKER_LOG.warn("Failed adding varbind " + vbs[i] + " for element " + getHostAddress() + ".", e);
            }
            return true;
        }

        @Override
        public void finished(final TreeEvent e) {
            if ((e.getVariableBindings() != null) && (e.getVariableBindings().length > 0)) {
                WALKER_LOG.debug("Element {} finished subtree count {}.", getHostAddress(), e.getVariableBindings().length);
                next(e);
            }

            if (!finished && advanceOidIndex()) {
                return;
            }

            final long walkTime = System.currentTimeMillis() - startTime;
            WALKER_LOG.debug("Element {} walked, requests: {}, objects: {} time: {}.", getHostAddress(), requests, objects, walkTime);

            if (e.isError()) {
                LOG.error("Exception while walking " + getHostAddress() + ".", e.getException());
                response = new WalkResponse(new WalkException(e.getErrorMessage()));
            } else {
                response = new WalkResponse(true);
                response.setObjectCount(objects);
                response.setRequestCount(requests);
                response.setWalkTime(walkTime);
            }

            finished = true;

            synchronized (this) {
                this.notify();
            }
        }

        @Override
        public boolean isFinished() {
            WALKER_LOG.debug("Element {} finished: {}", getHostAddress(), finished);
            return finished;
        }

        private boolean advanceOidIndex() {
            while (++oidIndex < oids.size()
                    && (lastProcessedOid != null && lastProcessedOid.compareTo(oids.get(oidIndex)) > 0)) {
                // Empty block
            }

            if (oidIndex < oids.size()) {
                WALKER_LOG.debug("Element {} next subtree: {}", getHostAddress(), oids.get(oidIndex));
                treeUtils.getSubtree(target, oids.get(oidIndex), this, this);
                return true;
            }

            return false;
        }



    }
}
