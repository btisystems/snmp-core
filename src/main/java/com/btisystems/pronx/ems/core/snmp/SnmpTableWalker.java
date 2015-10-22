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

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableListener;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Understands how to retrieval specific rows from a set of tables.
 */
public class SnmpTableWalker extends DefaultPDUFactory {

    private static final Logger log = LoggerFactory.getLogger(SnmpTableWalker.class);

    // More fine grained logging than whole class ...
    private static final Logger walkerLog = LoggerFactory.getLogger(ISnmpSession.class.getName() + ".walk");

    private final ISnmpConfiguration snmpConfiguration;
    private final Target target;
    private final Address address;
    private final Session snmpInterface;

    /**
     * Class constructor.
     *
     * @param snmpConfiguration the configuration to be used for the session
     * @param snmp              session to be wrapped
     * @param target            snmp4j's remote SNMP entity reference
     * @param address           address of the device associated with the session
     */
    public SnmpTableWalker(final ISnmpConfiguration snmpConfiguration,
                           final Session snmp,
                           final Target target,
                           final Address address) {
        this.snmpConfiguration = snmpConfiguration;
        this.snmpInterface = snmp;
        this.address = address;
        this.target = target;
    }

    /**
     * Gets table rows.
     *
     * @param networkDevice the network device
     * @param tableIndexes  the table indexes
     * @return the table rows
     * @throws IOException the io exception
     */
    public WalkResponse getTableRows(final IVariableBindingHandler networkDevice,
                                     final Map<DeviceEntityDescription, List<OID>> tableIndexes) throws IOException {

        final Iterator<TableIndexRetrievalDescriptor> retrievalIterator = getTableRetrievalDescriptors(tableIndexes).iterator();
        if (retrievalIterator.hasNext()) {

            final TableUtils tableUtils = newTableUtility();
            final TableResponseListener listener = new TableResponseListener(networkDevice);

            do {
                final TableIndexRetrievalDescriptor indexRetrieval = retrievalIterator.next();
                final WalkResponse response = retrieveRowsWithIndex(tableUtils, listener, indexRetrieval);
                if (response != null) {
                    return response;
                }
            } while (!listener.hadError() && retrievalIterator.hasNext());

            return listener.getResponse();
        }
        return new WalkResponse(new WalkException("Nothing to retrieve"));
    }

    private Collection<TableIndexRetrievalDescriptor> getTableRetrievalDescriptors(final Map<DeviceEntityDescription, List<OID>> tableIndexes) {

        final Map<OID, TableIndexRetrievalDescriptor> descriptorMap = new HashMap<OID, TableIndexRetrievalDescriptor>();

        for (final Entry<DeviceEntityDescription, List<OID>> entry : tableIndexes.entrySet()) {
            final DeviceEntityDescription deviceEntityDescription = entry.getKey();
            final Collection<FieldDescription> columnDescriptions = deviceEntityDescription.getFields();

            for (final OID indexOid : entry.getValue()) {

                TableIndexRetrievalDescriptor descriptor = descriptorMap.get(indexOid);
                if (descriptor == null) {
                    descriptor = new TableIndexRetrievalDescriptor(indexOid);
                    descriptorMap.put(indexOid, descriptor);
                }

                for (final FieldDescription columnDescription : columnDescriptions) {
                    final OID columnOid = new OID(deviceEntityDescription.getOid());
                    columnOid.append(columnDescription.getId());
                    log.debug("Add OID for column:{}", columnOid);
                    descriptor.columnOids.add(columnOid);
                }
            }
        }
        return descriptorMap.values();
    }

    // Create and configure Table walking utility.
    private TableUtils newTableUtility() {
        final TableUtils tableUtils = new TableUtils(snmpInterface, this);
        if (snmpConfiguration.getMaximumColumnsPerPdu() != 0) {
            tableUtils.setMaxNumColumnsPerPDU(snmpConfiguration.getMaximumColumnsPerPdu());
        }
        if (snmpConfiguration.getMaximumRowsPerPdu() != 0) {
            tableUtils.setMaxNumRowsPerPDU(snmpConfiguration.getMaximumRowsPerPdu());
        }
        return tableUtils;
    }

    private WalkResponse retrieveRowsWithIndex(final TableUtils tableUtils,
                                               final TableResponseListener listener,
                                               final TableIndexRetrievalDescriptor indexRetrieval) {

        walkerLog.debug("retrieve columns {} with index {}", indexRetrieval.getColumnOids(), indexRetrieval.lowIndex);

        listener.reset();
        synchronized (listener) {
            tableUtils.getTable(target, indexRetrieval.getColumnOids(), listener, null, indexRetrieval.lowIndex, indexRetrieval.highIndex);
            try {
                // Allow for possibility that listener has already terminated in this thread,
                // which it might have done if the initial Snmp.send threw an IOException
                if (!listener.isFinished()) {
                    final long startTime = System.currentTimeMillis();
                    while ((!listener.isFinished()) && ((System.currentTimeMillis() - startTime) < snmpConfiguration.getWalkTimeout())) {
                        listener.wait(snmpConfiguration.getWalkTimeout());
                    }

                    if (!listener.isFinished()) {
                        //Timed out rather than finished.
                        listener.stopWalk();
                        log.error("Table Walk for device {} timed out.", getHostAddress());
                        return new WalkResponse(new WalkException("Walk timed out"));
                    }
                }
            } catch (final InterruptedException ex) {
                Thread.interrupted();
                walkerLog.warn("listener wait interrupted:{}", ex);
            }
        }
        return null;
    }

    private String getHostAddress() {
        return ((IpAddress) address).getInetAddress().getHostAddress();
    }


    @Override
    public PDU createPDU(final Target target) {
        final PDU request = new PDU();
        request.setType(PDU.GETBULK);
        return request;
    }

    private class TableResponseListener implements TableListener {

        private final long startTime = System.currentTimeMillis();
        private final IVariableBindingHandler networkDevice;
        private boolean finished;
        private int requests;
        private int objects;
        private WalkResponse response;

        /**
         * Instantiates a new Table response listener.
         *
         * @param networkDevice the network device
         */
        public TableResponseListener(final IVariableBindingHandler networkDevice) {
            this.networkDevice = networkDevice;
            finished = false;
        }

        /**
         * Gets response.
         *
         * @return the response
         */
        public WalkResponse getResponse() {
            if (response != null) {
                return response;
            }
            if (!finished) {
                return new WalkResponse(new WalkException("Walk interrupted"));
            }


            final long walkTime = System.currentTimeMillis() - startTime;
            walkerLog.debug("requests:{}, objects:{}", requests, objects);
            walkerLog.debug("time:{}", walkTime);

            response = new WalkResponse(true);
            response.setObjectCount(objects);
            response.setRequestCount(requests);
            response.setWalkTime(walkTime);
            return response;
        }

        /**
         * Stop walk.
         */
        public void stopWalk() {
            finished = true;
        }        @Override
        public boolean next(final TableEvent e) {
            requests++;
            final VariableBinding[] vbs = e.getColumns();
            for (VariableBinding vb : vbs) {
                if (vb != null) {
                    addVariable(vb);
                }
                objects++;
            }
            return !finished;
        }

        /**
         * Had error boolean.
         *
         * @return the boolean
         */
        public boolean hadError() {
            return response != null;
        }        private boolean addVariable(final VariableBinding binding) {
            walkerLog.debug(">>> addVariable:{}", binding);
            try {
                final boolean wasAdded = networkDevice.addVariable(binding);
                if (!wasAdded) {
                    return false;
                }
            } catch (final Exception e) {
                walkerLog.warn("Exception adding variable binding:{} {} from " + getHostAddress(), binding, e);
            }
            return true;
        }

        /**
         * Reset.
         */
        public void reset() {
            finished = false;
        }        @Override
        public void finished(final TableEvent e) {
            walkerLog.debug("Finished table request");
            if ((e.getColumns() != null) && (e.getColumns().length > 0)) {
                next(e);
            }

            if (e.isError()) {
                log.error("Exception when walking:" + e.getErrorMessage(), e.getException());
                response = new WalkResponse(new WalkException(e.getErrorMessage()));
            }
            finished = true;
            synchronized (this) {
                this.notify();
            }
        }

        @Override
        public boolean isFinished() {
            walkerLog.debug("isFinished {}", finished);
            return finished;
        }






    }

    private class TableIndexRetrievalDescriptor {
        /**
         * The Low index.
         */
        protected OID lowIndex;
        /**
         * The High index.
         */
        protected OID highIndex;
        /**
         * The Column oids.
         */
        protected Set<OID> columnOids;
        /**
         * Instantiates a new Table index retrieval descriptor.
         *
         * @param indexOid the index oid
         */
        public TableIndexRetrievalDescriptor(final OID indexOid) {
            if (indexOid.last() == 0) {
                lowIndex = indexOid.trim();
            } else {
                lowIndex = new OID(indexOid);
                lowIndex.set(indexOid.size() - 1, indexOid.last() - 1);
            }

            highIndex = indexOid;
            columnOids = new TreeSet<OID>();
        }

        /**
         * Get column oids oid [ ].
         *
         * @return the oid [ ]
         */
        public OID[] getColumnOids() {
            return columnOids.toArray(new OID[columnOids.size()]);
        }
    }
}
