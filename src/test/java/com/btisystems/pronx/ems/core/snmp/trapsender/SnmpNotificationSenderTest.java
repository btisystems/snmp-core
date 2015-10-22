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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.ArrayList;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import com.btisystems.pronx.ems.core.snmp.ISnmpNotificationOidLookup;
import com.btisystems.pronx.ems.core.snmp.trapsender.TrapSender;
import com.btisystems.pronx.ems.core.snmp.trapsender.TrapRecipient;
import com.btisystems.pronx.ems.core.model.INotification;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

@RunWith(value=PowerMockRunner.class)
@PrepareForTest({TrapSender.class, Snmp.class})
@PowerMockIgnore({"javax.management.*"}) 
public class SnmpNotificationSenderTest {
    private TrapSender sender;
    private ArrayList<TrapRecipient> receivers;
    private TrapRecipient receiver;
    private INotification notification;
    private Snmp mockSnmp;
    private Capture<PDU> pduCapture;
    private Capture<Target> targetCapture;
    private DefaultUdpTransportMapping mockUdp;
    private ISnmpNotificationOidLookup mockSnmpNotificationOidLookup;

    public SnmpNotificationSenderTest() {
    }

    @Before
    public void setUp() {
        mockSnmp = PowerMock.createMock(Snmp.class);
        mockUdp = PowerMock.createMock(DefaultUdpTransportMapping.class);
        mockSnmpNotificationOidLookup = PowerMock.createMock(ISnmpNotificationOidLookup.class);

        pduCapture = new Capture<PDU>();
        targetCapture = new Capture<Target>();

        receivers = new ArrayList<TrapRecipient>();
        receiver = new TrapRecipient();
        receiver.setIpAddress("10.10.10.10");
        receiver.setCommunity("community");
        receiver.setPort(162);
        receivers.add(receiver);

        notification = new Notification(1, "172.27.7.101", "string", -1);
        sender = new TrapSender();

    }

    @Test
    public void shouldSendTrap() throws Exception {
        expectNewSnmp();

        expect(mockSnmp.send(capture(pduCapture), capture(targetCapture))).andReturn(null);

        final Capture<FieldDescription> fieldDesriptionCapture = new Capture<FieldDescription>();
        expect(mockSnmpNotificationOidLookup.lookupOidForField(capture(fieldDesriptionCapture))).andAnswer(new IAnswer<OID>() {
			@Override
			public OID answer() throws Throwable {
				final FieldDescription description = fieldDesriptionCapture.getValue();
				return new OID("1").append(description.getId());
			}
        }).anyTimes();

        replayAll();

        sender.send(notification, mockSnmpNotificationOidLookup, receivers);

        verifyAll();

        assertTrue(targetCapture.getValue().getAddress().toString().contains(receiver.getIpAddress()));
        assertTrue(targetCapture.getValue().getAddress().toString().contains(Integer.toString(receiver.getPort())));

        assertEquals(notification.get_Description().getOid().toString(), pduCapture.getValue().get(1).getVariable().toString());
        assertEquals("1", pduCapture.getValue().get(2).getVariable().toString());
        assertEquals("172.27.7.101", pduCapture.getValue().get(3).getVariable().toString());
        assertEquals("string", pduCapture.getValue().get(4).getVariable().toString());
        assertEquals(new UnsignedInteger32(-1), pduCapture.getValue().get(5).getVariable());
    }

    @Test
    public void shouldOnlyInitSnmpOnce() throws Exception {
        expectNewSnmp();

        final Capture<FieldDescription> fieldDesriptionCapture = new Capture<FieldDescription>();
        expect(mockSnmpNotificationOidLookup.lookupOidForField(capture(fieldDesriptionCapture))).andAnswer(new IAnswer<OID>() {
			@Override
			public OID answer() throws Throwable {
				final FieldDescription description = fieldDesriptionCapture.getValue();
				return new OID("1").append(description.getId());
			}
        }).anyTimes();

        expect(mockSnmp.send(capture(pduCapture), capture(targetCapture))).andReturn(null).times(2);

        replayAll();

        sender.send(notification, mockSnmpNotificationOidLookup, receivers);
        sender.send(notification, mockSnmpNotificationOidLookup, receivers);

        verifyAll();
    }

    private void expectNewSnmp() throws Exception {
        expectNew(DefaultUdpTransportMapping.class).andReturn(mockUdp);
        expectNew(Snmp.class, mockUdp).andReturn(mockSnmp);
    }




}
