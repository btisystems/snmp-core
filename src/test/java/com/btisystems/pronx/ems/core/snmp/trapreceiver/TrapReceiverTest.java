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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.util.MultiThreadedMessageDispatcher;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Snmp.class, TrapReceiver.class, CommandResponderEvent.class})
public class TrapReceiverTest {
    private TrapReceiver receiver;
    private TrapReceiverConfiguration config;
    @Mock private ITrapHandlerService mockService;
    private Snmp mockSnmp;
    private CommandResponderEvent mockEvent;
    private PDU pdu;
    
    public TrapReceiverTest() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockSnmp = PowerMockito.mock(Snmp.class);
        mockEvent = PowerMockito.mock(CommandResponderEvent.class);
        
        receiver = new TrapReceiver();
        
        config = new TrapReceiverConfiguration();
        config.setDispatcherThreadCount(1);
        config.setListeningAddress("0.0.0.0");
        config.setAddressMapper(null);
        config.setTrapHandlerService(mockService);
        
        pdu = new PDU();
        pdu.setType(PDU.V1TRAP);
        
        receiver.setConfiguration(config);
    }

    @Test
    public void shouldStartListening() throws Exception {       
        PowerMockito.whenNew(Snmp.class)
                .withParameterTypes(MessageDispatcher.class)
                .withArguments(Mockito.isA(MultiThreadedMessageDispatcher.class))
                .thenReturn(mockSnmp);
        
        Mockito.when(mockSnmp.addNotificationListener((Address)Mockito.isNotNull(), (CommandResponder)Mockito.isNotNull())).thenReturn(Boolean.TRUE);
        
        respondWithPDU();
        
        receiver.startReceiving();
        
        Mockito.verify(mockService).handle((Date)Mockito.isNotNull(), Mockito.eq("1.1.1.1"), Mockito.eq(pdu));
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailInvalidAddress() throws Exception {
        config.setListeningAddress("not an address");
        receiver.setConfiguration(config);
        
    }

    private void respondWithPDU() throws IOException {
        PowerMockito.when(mockEvent.getPDU()).thenReturn(pdu);
        PowerMockito.when(mockEvent.getPeerAddress()).thenReturn(new IpAddress("1.1.1.1")); 

        Mockito.doAnswer(new Answer() {
            private final Thread thread = Thread.currentThread();
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                    
                    @Override
                    public void run() {
                        try {
                            receiver.processPdu(mockEvent);
                        } catch (Exception e){
                            e.printStackTrace();
                            fail(e.getMessage());
                        } finally {
                            thread.interrupt(); //Ensure the test exits.
                        }
                    }
                }, 1, TimeUnit.SECONDS);
                return null;
            }
        }).when(mockSnmp).listen();
    }
    
}
