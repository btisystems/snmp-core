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
package com.btisystems.pronx.ems.core.snmp.trapreceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.util.Date;

/**
 * The type Trap receiver.
 */
public class TrapReceiver implements ITrapReceiver {

    /**
     * The constant SLASH.
     */
    public static final char SLASH = '/';
    /**
     * The constant PORT.
     */
    public static final String PORT = "/161";
    private static final Logger LOG = LoggerFactory.getLogger(TrapReceiver.class);
    private static final String TCP_TRANSPORT = "tcp";
    private static final String UDP_TRANSPORT = "udp";
    private static OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
    /**
     * The Config.
     */
    protected ITrapReceiverConfiguration config;
    /**
     * The Address mapper.
     */
    protected ITrapSourceMapper addressMapper;
    /**
     * The Listening address.
     */
    protected Address listeningAddress;
    /**
     * The Trap handler service.
     */
    protected ITrapHandlerService trapHandlerService;
    /**
     * The Dispatcher thread count.
     */
    protected int dispatcherThreadCount;
    private OctetString authoritativeEngineID;

    @Override
    public synchronized void setConfiguration(final ITrapReceiverConfiguration config) {
        this.config = config;
        this.listeningAddress = getAddress(config.getListeningAddress());
        this.trapHandlerService = config.getTrapHandlerService();
        this.addressMapper = config.getAddressMapper();
        this.dispatcherThreadCount = config.getDispatcherThreadCount();
    }

    @Override
    public synchronized void startReceiving() {
        LOG.debug(">>> receiveTraps");
        try {
            final ThreadPool threadPool = ThreadPool.create("DispatcherPool", config.getDispatcherThreadCount());
            final MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool,
                    new MessageDispatcherImpl());

            // add message processing models
            mtDispatcher.addMessageProcessingModel(new MPv1());
            mtDispatcher.addMessageProcessingModel(new MPv2c());
            mtDispatcher.addMessageProcessingModel(new MPv3(localEngineID.getValue()));

            // add all security protocols
            SecurityProtocols.getInstance().addDefaultProtocols();
            SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

            final Snmp snmp = new Snmp(mtDispatcher);
            final USM usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            if (authoritativeEngineID != null) {
                snmp.setLocalEngine(authoritativeEngineID.getValue(), 0, 0);
            }

            snmp.addNotificationListener(listeningAddress, this);

            snmp.listen();
            LOG.info("Listening for notifications on {}", listeningAddress);

            waitWhileListening();
        } catch (final IOException e) {
            LOG.debug("Failed listening for traps:", e);
            LOG.warn("IOException listening for traps:{}", e.getMessage());
        }
    }

    private synchronized void waitWhileListening() {
        try {
            while (true) {
                this.wait();
            }
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gets address.
     *
     * @param transportAddress the transport address
     * @return the address
     */
    protected Address getAddress(final String transportAddress) {
        String transport = UDP_TRANSPORT;
        String modifiedAddress = transportAddress;
        final int colon = modifiedAddress.indexOf(':');

        if (colon > 0) {
            transport = modifiedAddress.substring(0, colon);
            modifiedAddress = modifiedAddress.substring(colon + 1);
        }
        // set default port
        if (modifiedAddress.indexOf(SLASH) < 0) {
            modifiedAddress += PORT;
        }
        if (transport.equalsIgnoreCase(UDP_TRANSPORT)) {
            return new UdpAddress(modifiedAddress);
        } else if (transport.equalsIgnoreCase(TCP_TRANSPORT)) {
            return new TcpAddress(modifiedAddress);
        }

        throw new IllegalArgumentException("Unknown transport " + transport);
    }

    @Override
    public synchronized void processPdu(final CommandResponderEvent e) {
        final Date timeNow = new Date();
        final PDU command = e.getPDU();
        final String remoteAddress = getRemoteAddress(e);
        final String resolvedAddress = addressMapper.mapAddress(remoteAddress);
        e.setProcessed(true);
        if (command != null) {
            switch (command.getType()) {
                case PDU.TRAP:
                case PDU.INFORM:
                case PDU.V1TRAP:
                    processNotification(remoteAddress, command, timeNow, resolvedAddress);
                    break;
                default:
                    LOG.warn("Unsupported PDU from:" + remoteAddress + ":" + command.toString());
            }
        }
    }

    private String getRemoteAddress(final CommandResponderEvent e) {
        return ((IpAddress) e.getPeerAddress()).getInetAddress().getHostAddress();
    }

    private void processNotification(final String remoteAddress, final PDU command, final Date timeNow, final String resolvedAddress) {
        // log here in case issues when sending it to service
        LOG.debug("Trap received from address = {} containing command = {}", remoteAddress, command);
        trapHandlerService.handle(timeNow, resolvedAddress, command);
    }

}
