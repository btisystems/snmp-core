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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class V2cSnmpConfigurationTest
{

    @Test
    public void shouldCreateTarget()   {

        final V2cSnmpConfiguration configuration = new V2cSnmpConfiguration();
        configuration.setCommunity("public");

        configuration.setRetries(3);
        configuration.setTimeout(10000);
        configuration.setMaxSizeResponsePDU(9999);

        final Target target = configuration.createTarget(new UdpAddress("127.0.0.1/161"));

        assertEquals(3, target.getRetries());
        assertEquals(10000, target.getTimeout());
        assertEquals(SnmpConstants.version2c, target.getVersion());
        assertEquals(9999, target.getMaxSizeRequestPDU());
    }

    @Test
    public void shouldCreatePDU()  {

        final V2cSnmpConfiguration configuration = new V2cSnmpConfiguration();
        configuration.setCommunity("public");

        configuration.setMaxRepetitions(3);
        configuration.setNonRepeaters(22);

        final PDU getBulkPdu = configuration.createPDU(PDU.GETBULK);
        final PDU getPdu = configuration.createPDU(PDU.GET);
        final PDU setPdu = configuration.createPDU(PDU.SET);

        assertEquals(PDU.GETBULK, getBulkPdu.getType());
        assertEquals(3, getBulkPdu.getMaxRepetitions());
        assertEquals(22, getBulkPdu.getNonRepeaters());
        assertEquals(PDU.GET, getPdu.getType());
        assertEquals(PDU.SET, setPdu.getType());
    }

    @Test
    public void shouldCreateSnmpSession() throws IOException   {

        final V2cSnmpConfiguration configuration = new V2cSnmpConfiguration();

        final Session session = configuration.createSnmpSession(new DefaultUdpTransportMapping());
        assertNotNull(session);
    }
}
