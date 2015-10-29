/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.btisystems.pronx.ems.core.snmp.trapreceiver;

import java.io.IOException;
import java.util.Date;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;

 
public class TrapReceiverTest {
    private TrapReceiver receiver;
    private TrapReceiverConfiguration config;
    @Mock private ITrapHandlerService mockService;
    @Mock private Snmp mockSnmp;
    @Mock private CommandResponderEvent mockEvent;
    private PDU pdu;
    
    public TrapReceiverTest() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        receiver = new TrapReceiver(){

            @Override
            protected synchronized void waitWhileListening() {}

            @Override
            protected Snmp createSnmp(final MessageDispatcher mtDispatcher) {
                return mockSnmp;
            }
            
        };
        
        config = new TrapReceiverConfiguration();
        config.setDispatcherThreadCount(1);
        config.setListeningAddress("0.0.0.0");
        config.setTrapHandlerService(mockService);
        config.setAddressMapper(new ITrapSourceMapper() {
            @Override
            public String mapAddress(final String sourceAddress) {
                return sourceAddress;
            }
        });
        
        pdu = new PDU();
        pdu.setType(PDU.V1TRAP);
        
        receiver.setConfiguration(config);
    }

    @Test
    public void shouldStartListening() throws Exception {       
        Mockito.when(mockSnmp.addNotificationListener((Address)Mockito.isNotNull(), (CommandResponder)Mockito.isNotNull())).thenReturn(Boolean.TRUE);
        respondWithPDU();
        
        receiver.startReceiving();
        
        Mockito.verify(mockService).handle((Date)Mockito.isNotNull(), Mockito.eq("1.1.1.1"), Mockito.eq(pdu));
    }
    
    @Test
    public void shouldHandleExceptionWhenListening() throws Exception {       
        Mockito.when(mockSnmp.addNotificationListener((Address)Mockito.isNotNull(), (CommandResponder)Mockito.isNotNull())).thenReturn(Boolean.TRUE);
        Mockito.doThrow(new IOException()).when(mockSnmp).listen();
        
        receiver.startReceiving();
        
        Mockito.verifyZeroInteractions(mockService);
    }
    
    @Test
    public void shouldIgnoreUnknownPDUType() throws Exception {       
        Mockito.when(mockSnmp.addNotificationListener((Address)Mockito.isNotNull(), (CommandResponder)Mockito.isNotNull())).thenReturn(Boolean.TRUE);
        respondWithPDU();
        pdu.setType(-1);
        
        receiver.startReceiving();
        
        Mockito.verifyZeroInteractions(mockService);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailInvalidAddress() throws Exception {
        config.setListeningAddress("not: an address");
        receiver.setConfiguration(config);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnNullDispatcher() throws Exception {
        receiver = new TrapReceiver();
        receiver.createSnmp(null);
    }
    
    @Test
    public void shouldCreateSnmp() throws Exception {
        receiver = new TrapReceiver();
        assertNotNull(receiver.createSnmp(new MessageDispatcherImpl()));
    }

    private void respondWithPDU() throws IOException {
        Mockito.when(mockEvent.getPDU()).thenReturn(pdu);
        Mockito.when(mockEvent.getPeerAddress()).thenReturn(new IpAddress("1.1.1.1")); 

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                receiver.processPdu(mockEvent);
                return null;
            }
        }).when(mockSnmp).listen();
    }
    
}
