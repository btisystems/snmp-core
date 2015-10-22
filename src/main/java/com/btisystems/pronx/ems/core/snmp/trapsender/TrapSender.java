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
package com.btisystems.pronx.ems.core.snmp.trapsender;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import com.btisystems.pronx.ems.core.model.INotification;
import com.btisystems.pronx.ems.core.snmp.ISnmpNotificationOidLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The type Trap sender.
 */
public class TrapSender implements ITrapSender {

    private static final Logger LOG = LoggerFactory.getLogger(TrapSender.class);
    private Snmp snmp;

    @Override
    public void send(final INotification notification, final ISnmpNotificationOidLookup lookup, final List<TrapRecipient> trapRecipients) {
        final PDU trap = new PDU();

        trap.setType(PDU.TRAP);

        final TimeTicks uptime = new TimeTicks();
        uptime.fromMilliseconds(ManagementFactory.getRuntimeMXBean().getUptime());

        trap.add(new VariableBinding(SnmpConstants.sysUpTime, uptime));
        trap.add(new VariableBinding(SnmpConstants.snmpTrapOID, notification.get_Description().getOid()));

        buildPayload(notification, lookup, trap);

        for (TrapRecipient trapReceiver : trapRecipients) {
            sendTrap(trapReceiver, trap);
        }

    }

    private void buildPayload(final INotification notification, final ISnmpNotificationOidLookup lookup, final PDU trap) {
        final Map<String, VariableBinding> variables = new TreeMap<String, VariableBinding>(new VarBindComparator());
        for (FieldDescription fieldDescription : notification.get_Description().getFields()) {
            final Variable variable = buildVarBind(fieldDescription, notification);
            if (variable != null) {
                final OID oid = lookup.lookupOidForField(fieldDescription);
                variables.put(oid.toString(), new VariableBinding(oid, variable));
            }
        }
        if (!variables.isEmpty()) {
            trap.addAll(variables.values().toArray(new VariableBinding[variables.size()]));
        }
    }

    private void sendTrap(final TrapRecipient trapReceiver, final PDU trap) {

        try {
            // Specify receiver
            final Address targetaddress = new UdpAddress(InetAddress.getByName(trapReceiver.getIpAddress()), trapReceiver.getPort());
            final CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(trapReceiver.getCommunity()));
            target.setVersion(SnmpConstants.version2c);
            target.setAddress(targetaddress);

            createSnmpSession();

            LOG.trace("Sending trap:{} to {} ...", trap, trapReceiver);

            snmp.send(trap, target);

        } catch (IOException ex) {
            LOG.error("Error sending trap:" + trap + " to receiver:" + trapReceiver, ex);
        }
    }

    private Variable buildVarBind(final FieldDescription fieldDescription, final INotification notification) {
        Variable result = null;
        switch (fieldDescription.getType()) {
            case FIXED_X10:
            case FIXED_X100:
            case FIXED_X1000:
            case INTEGER:
                result = buildNumber(notification, fieldDescription);
                break;
            case STRING:
            case BITS:
            case OID:
                result = buildString(notification, fieldDescription);
                break;
            case DATE_AND_TIME:
                result = buildDateTime(notification, fieldDescription);
                break;
            case IP_ADDRESS:
                result = buildIPAddress(notification, fieldDescription);
                break;
            case UNSIGNED32:
                result = buildUnsigned(notification, fieldDescription);
                break;
            case ENTITY:
            case TABLE:
            default:
                LOG.error("Unexpected type in Notification: {}", fieldDescription);
        }
        return result;

    }

    private void createSnmpSession() throws IOException {
        if (snmp == null) {
            snmp = new Snmp(new DefaultUdpTransportMapping());
        }
    }

    private Variable buildNumber(final INotification notification, final FieldDescription fieldDescription) {
        Variable result;
        result = new Integer32(notification.getInt(fieldDescription.getName()));
        return result;
    }

    private Variable buildString(final INotification notification, final FieldDescription fieldDescription) {
        Variable result = null;
        if (notification.getString(fieldDescription.getName()) != null) {
            result = new OctetString(notification.getString(fieldDescription.getName()));
        }
        return result;
    }

    private Variable buildDateTime(final INotification notification, final FieldDescription fieldDescription) {
        Variable result = null;
        if (notification.getString(fieldDescription.getName()) != null) {
            result = OctetString.fromHexString(notification.getString(fieldDescription.getName()), ':');
        }
        return result;
    }

    private Variable buildIPAddress(final INotification notification, final FieldDescription fieldDescription) {
        Variable result = null;
        if (notification.getString(fieldDescription.getName()) != null) {
            result = new IpAddress(notification.getString(fieldDescription.getName()));
        }
        return result;
    }

    private Variable buildUnsigned(final INotification notification, final FieldDescription fieldDescription) {
        Variable result;
        result = new UnsignedInteger32(notification.getInt(fieldDescription.getName()));
        return result;
    }
}
