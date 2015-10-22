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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

/**
 * V2c SNMP Configuration
 */

public class V2cSnmpConfiguration extends SnmpConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(V2cSnmpConfiguration.class);

    /**
     * Class constructor
     */
    public V2cSnmpConfiguration() {
        LOG.debug(">>> V2cSnmpConfiguration");
        version = SnmpConstants.version2c;
    }

    @Override
    public Target createTarget(final Address address) {
        final CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(getCommunity()));
        target.setAddress(address);
        target.setVersion(getVersion());
        target.setRetries(getRetries());
        target.setTimeout(getTimeout());
        target.setMaxSizeRequestPDU(getMaxSizeResponsePDU());
        return target;
    }

    @Override
    public PDU createPDU(final int type) {
        final PDU pdu = new PDU();
        pdu.setType(type);
        switch (type) {
            case PDU.GETBULK:
                pdu.setMaxRepetitions(getMaxRepetitions());
                pdu.setNonRepeaters(getNonRepeaters());
                break;
            default:
                LOG.trace("Not setting up non-default configuration for PDU type {}.", type);
        }
        return pdu;
    }

    @Override
    public Snmp createSnmpSession(final TransportMapping transportMapping) throws IOException {
        final ThreadPool threadPool = ThreadPool.create("SnmpDispatcherPool", getDispatcherPoolSize());
        final MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

        // Add message processing models
        mtDispatcher.addMessageProcessingModel(new MPv2c());

        final Snmp snmp = new Snmp(mtDispatcher, transportMapping);

        snmp.listen();
        return snmp;
    }
}