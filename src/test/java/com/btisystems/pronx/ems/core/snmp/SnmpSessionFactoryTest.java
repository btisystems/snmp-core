/*
 * Copyright 2015 hshorter.
 *
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
package com.btisystems.pronx.ems.core.snmp;

import java.io.IOException;
import org.easymock.Capture;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;

public class SnmpSessionFactoryTest
{
    private ISnmpConfigurationFactory defaultConfigurationFactory;
    private ISnmpConfigurationFactory alternativeConfigurationFactory;
    private ISnmpConfiguration defaultConfiguration;
    private ISnmpConfiguration alternativeConfiguration;
    private Session snmpInterface;
    private Target target;

    private SnmpSessionFactory factory;

    @Before
    public void setUp() throws Exception {
        defaultConfigurationFactory = createMock(ISnmpConfigurationFactory.class);
        alternativeConfigurationFactory = createMock(ISnmpConfigurationFactory.class);
        defaultConfiguration = createMock(ISnmpConfiguration.class);
        alternativeConfiguration = createMock(ISnmpConfiguration.class);
        snmpInterface = createMock(Session.class);
        target = createMock(Target.class);

        factory = new SnmpSessionFactory(defaultConfigurationFactory);

    }

    @Test
    public void shouldCreateSession() throws IOException {
        expectSessionInteractions();
        expect(defaultConfiguration.createTarget(isA(Address.class))).andReturn(target);
        replayAll();

        final ISnmpSession session = factory.createSession(LOCALHOST, "community");
        assertEquals("localhost", session.getAddress().getHostName());

        verifyAll();
    }
    private static final String LOCALHOST = "localhost";

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSupportAlternativeConfigs() throws IOException  {
        factory.createSession(LOCALHOST, "community", "factory", ISnmpConfigurationFactory.AccessType.READ_ONLY);
    }


    @Test
    public void shouldUseExplicitPort() throws IOException {
        expectSessionInteractions();
        final Capture<Address> addressCapture = expectCreateTarget();

        replayAll();

        factory.createSession(defaultConfiguration, "localhost/9999");

        verifyAll();

        final UdpAddress address = (UdpAddress) addressCapture.getValue();
        assertEquals(9999, address.getPort());
    }

    @Test
    public void shouldAcceptExplicitUdpProtocol() throws IOException {
        expectSessionInteractions();
        final Capture<Address> addressCapture = expectCreateTarget();

        replayAll();

        factory.createSession(defaultConfiguration, "udp:localhost/9999");

        verifyAll();

        final UdpAddress address = (UdpAddress) addressCapture.getValue();
        assertEquals(9999, address.getPort());
    }

    @Test
    public void shouldAcceptTcpProtocol() throws IOException {
        expectSessionInteractions();
        final Capture<Address> addressCapture = expectCreateTarget();

        replayAll();

        factory.createSession(defaultConfiguration, "tcp:localhost");

        verifyAll();

        final TcpAddress address = (TcpAddress) addressCapture.getValue();
        assertEquals(161, address.getPort());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptInvalidProtocol() throws IOException  {
        expect(defaultConfiguration.getPort()).andReturn(161);
        replayAll();
        factory.createSession(defaultConfiguration, "notAProtocol:localhost");

    }

    private void replayAll() {
        replay(defaultConfigurationFactory);
        replay(alternativeConfigurationFactory);
        replay(alternativeConfiguration);
        replay(defaultConfiguration);
        replay(snmpInterface);
        replay(target);
    }

    private void verifyAll() {
        verify(defaultConfigurationFactory);
        verify(alternativeConfigurationFactory);
        verify(alternativeConfiguration);
        verify(defaultConfiguration);
        verify(snmpInterface);
        verify(target);
    }

    private Capture<Address> expectCreateTarget() {
        final Capture<Address> result = new Capture<>();
        expect(defaultConfiguration.createTarget(capture(result))).andReturn(target);
        return result;
    }

    private void expectSessionInteractions() throws IOException {
        expect(defaultConfigurationFactory.getConfiguration(ISnmpConfigurationFactory.AccessType.READ_WRITE)).andReturn(defaultConfiguration).atLeastOnce();
        expect(defaultConfiguration.createSnmpSession(isA(TransportMapping.class))).andReturn(snmpInterface);
        expect(defaultConfiguration.getPort()).andReturn(161);
    }
}
