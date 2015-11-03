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
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.btisystems.pronx.ems.core.snmp.ISnmpConfigurationFactory.AccessType;


public class SnmpSessionFactory implements ISnmpSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpSessionFactory.class);

    private ISnmpConfigurationFactory configurationFactory;
    private Session defaultSnmpInterface;

    public SnmpSessionFactory(final ISnmpConfigurationFactory defaultConfigurationFactory) {
        this.configurationFactory = defaultConfigurationFactory;
    }

    @Override
    public ISnmpSession createSession(final ISnmpConfiguration configuration, final String ipAddress) throws IOException {
        LOG.debug(">>> createSession address:{}", ipAddress);
        final Address address = getAddress(ipAddress, configuration.getPort());
        final Session snmpInterface = configurationFactory.getConfiguration(AccessType.READ_ONLY).createSnmpSession(new DefaultUdpTransportMapping());
        final Target target = configuration.createTarget(address);
        return new SnmpSession(configuration, snmpInterface, target, address);
    }

    @Override
    public ISnmpSession createSession(final String ipAddress, final String communityString) throws IOException {
        LOG.debug(">>> createSession address:{}", ipAddress);
        final ISnmpConfiguration configuration = configurationFactory.getConfiguration(AccessType.READ_WRITE);
        if (configuration instanceof V2cSnmpConfiguration && communityString != null) {
            ((V2cSnmpConfiguration) configuration).setCommunity(communityString);
        }
        return createSession(configuration, ipAddress);
    }

    @Override
    public ISnmpSession createSession(final String ipAddress,
                                                  final String communityString,
                                                  final String factoryName,
                                                  final AccessType accessType) throws IOException {

        LOG.debug(">>> createSessionUsingFactory address:{} {}", ipAddress, factoryName);

        final ISnmpConfiguration configuration = configurationFactory.getConfiguration(accessType);
        if (configuration instanceof V2cSnmpConfiguration && communityString != null) {
            ((V2cSnmpConfiguration) configuration).setCommunity(communityString);
        }
        return createSession(configuration, ipAddress);
    }

    private Address getAddress(final String transportAddress, final int port) {
        String address = transportAddress;
        String transport = "udp";
        final int colon = address.indexOf(':');
        if (colon > 0) {
            transport = address.substring(0, colon);
            address = address.substring(colon + 1);
        }
        // set default port
        if (address.indexOf('/') < 0) {
            address += "/" + port;
        }
        if (transport.equalsIgnoreCase("udp")) {
            return new UdpAddress(address);
        } else if (transport.equalsIgnoreCase("tcp")) {
            return new TcpAddress(address);
        }
        throw new IllegalArgumentException("Unknown transport " + transport);
    }

    private Session getDefaultSnmpInterface() throws IOException {
        if (defaultSnmpInterface == null) {
            synchronized (configurationFactory) {
                if (defaultSnmpInterface == null) {
                    final ISnmpConfiguration defaultSnmpConfiguration = configurationFactory.getConfiguration(AccessType.READ_WRITE);
                    defaultSnmpInterface =  defaultSnmpConfiguration.createSnmpSession(new DefaultUdpTransportMapping());
                }
            }
        }
        return defaultSnmpInterface;
    }
}
