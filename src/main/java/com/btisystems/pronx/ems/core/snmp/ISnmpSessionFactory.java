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

import com.btisystems.pronx.ems.core.snmp.ISnmpConfigurationFactory.AccessType;

import java.io.IOException;


/**
 * Factory for creating and configuring {@link ISnmpSession}s.
 */
public interface ISnmpSessionFactory {

    /**
     * Creates and configures an SNMP session
     *
     * @param configuration the configuration to be applied to the session
     * @param ipAddress     the ip address of the target device of the session
     * @return an {@link ISnmpSession} connected to the host at                      <code>ipAddress</code> and configured using <code>configuration</code>
     * @throws IOException as thrown by the underlying network interface
     */
    ISnmpSession createSession(ISnmpConfiguration configuration, String ipAddress) throws IOException;

    /**
     * Creates an SNMP session with a default configuration and an optional community string.
     *
     * @param ipAddress the ip address of the target device of the session
     * @param community community string to be used for the session, or null if the default is                      to be used.
     * @return an {@link ISnmpSession} connected to the host at                      <code>ipAddress</code> and configured using the default configuration
     * @throws IOException as thrown by the underlying network interface
     */
    ISnmpSession createSession(String ipAddress, String community) throws IOException;

    /**
     * Creates an SNMP session using a named configuration factory and an optional community string
     *
     * @param ipAddress   the ip address of the target device of the session
     * @param community   community string to be used for the session, or null if the default is                      to be used.
     * @param factoryName name of the configuration factory used to create the session configuration
     * @param accessType  type of snmp access required
     * @return an {@link ISnmpSession} connected to the host at                      <code>ipAddress</code> and configured using a configuration delivered by the                      named factory
     * @throws IOException as thrown by the underlying network interface
     */
    ISnmpSession createSession(String ipAddress, String community, String factoryName, AccessType accessType) throws IOException;
}
