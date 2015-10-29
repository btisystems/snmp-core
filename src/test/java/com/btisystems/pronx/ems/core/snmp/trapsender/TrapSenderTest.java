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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UnsignedInteger32;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import com.btisystems.pronx.ems.core.snmp.ISnmpNotificationOidLookup;
import com.btisystems.pronx.ems.core.model.INotification;
import java.io.IOException;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The type Snmp notification sender test.
 */
public class TrapSenderTest {
    private TrapSender sender;
    private ArrayList<TrapRecipient> receivers;
    private TrapRecipient receiver;
    private INotification notification;
    private ArgumentCaptor<PDU> pduCapture;
    private ArgumentCaptor<Target> targetCapture;
    private ArgumentCaptor<FieldDescription> fieldDesriptionCapture;
    
    @Mock private Snmp mockSnmp;
    @Mock private ISnmpNotificationOidLookup mockSnmpNotificationOidLookup;

    /**
     * Instantiates a new Snmp notification sender test.
     */
    public TrapSenderTest() {
    }

    /**
     * Sets up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        pduCapture = ArgumentCaptor.forClass(PDU.class);
        targetCapture = ArgumentCaptor.forClass(Target.class);
        fieldDesriptionCapture = ArgumentCaptor.forClass(FieldDescription.class);
        receivers = new ArrayList<>();
        receiver = new TrapRecipient();
        receiver.setIpAddress("10.10.10.10");
        receiver.setCommunity("community");
        receiver.setPort(162);
        receivers.add(receiver);

        notification = new Notification(1, "172.27.7.101", "string", -1, "1");
        sender = new TrapSender(){

            @Override
            protected void createSnmpSession() throws IOException {
                snmp = mockSnmp;
            }
            
        };

    }

    
    @Test
    public void shouldSendTrap() throws Exception {
        answerLookup();

        sender.send(notification, mockSnmpNotificationOidLookup, receivers);
        
        Mockito.verify(mockSnmp).send(pduCapture.capture(), targetCapture.capture());
        
        assertTrue(targetCapture.getValue().getAddress().toString().contains(receiver.getIpAddress()));
        assertTrue(targetCapture.getValue().getAddress().toString().contains(Integer.toString(receiver.getPort())));

        assertEquals(notification.get_Description().getOid().toString(), pduCapture.getValue().get(1).getVariable().toString());
        assertEquals("1", pduCapture.getValue().get(2).getVariable().toString());
        assertEquals("172.27.7.101", pduCapture.getValue().get(3).getVariable().toString());
        assertEquals("string", pduCapture.getValue().get(4).getVariable().toString());
        assertEquals("4294967295", pduCapture.getValue().get(5).getVariable().toString());
        assertEquals(new UnsignedInteger32(-1), pduCapture.getValue().get(5).getVariable());
    }
    
    @Test
    public void shouldContinueSendingAfterException() throws Exception {
        answerLookup();

        Mockito.when(mockSnmp.send(pduCapture.capture(), targetCapture.capture())).thenThrow(new IOException());
        
        sender.send(notification, mockSnmpNotificationOidLookup, receivers);
        sender.send(notification, mockSnmpNotificationOidLookup, receivers);
        
        Mockito.verify(mockSnmp, Mockito.times(2)).send(pduCapture.capture(), targetCapture.capture());
      
    }

    @Test
    public void shouldSendTwoOnSameSession() throws Exception {
        answerLookup();
        
        sender.send(notification, mockSnmpNotificationOidLookup, receivers);
        sender.send(notification, mockSnmpNotificationOidLookup, receivers);

        Mockito.verify(mockSnmp, Mockito.times(2)).send(pduCapture.capture(), targetCapture.capture());
    }
    
    @Test
    public void shouldInitRealSnmpSession() throws Exception {
        sender = new TrapSender();
        
        sender.createSnmpSession();
        Assert.assertNotNull(sender.snmp);
    }
    
    private void answerLookup() {
        Mockito.when(mockSnmpNotificationOidLookup.lookupOidForField(fieldDesriptionCapture.capture())).thenAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final FieldDescription description = fieldDesriptionCapture.getValue();
                return new OID("1").append(description.getId());
            }
        });
    }
    

}
