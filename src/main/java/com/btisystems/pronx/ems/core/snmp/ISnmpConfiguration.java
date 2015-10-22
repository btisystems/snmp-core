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
import java.io.Serializable;

import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;

/**
 * A configuration to be applied to an Snmp Session.
 */

public interface ISnmpConfiguration extends Serializable {

    /**
     * Deliver a {@link Target} built from the configuration
     *
     * @param address   the address of the device with which the target is to be associated
     * @return  a {@link Target} from the configuration
     */
    Target createTarget(Address address);

    /**
     * Deliver a {@link PDU} built from the configuration
     *
     * @param type  the request type
     * @return  a {@link PDU}, built from the configuration with the specified request type
     */
    PDU createPDU(int type);

    /**
     * Deliver a {@link Session} for the specified transport mapping
     *
     * @param transportMapping  the initial transport mapping
     * @return a {@link Session} instance
     * @throws IOException
     */
    Session createSnmpSession(TransportMapping transportMapping) throws IOException;

    /**
     * @return the SNMP version supported by the configuration.
     */
    int getVersion();

    /**
     * @return the total walk timeout.
     */
    int getWalkTimeout();

    /**
     * 
     * @param community 
     */
    void setCommunity(String community);

    /**
     * 
     * @return the Community string used to connect to the remote SNMP agent.
     */
    String getCommunity();

    /**
     * 
     * @return the number of objects that should be returned for all
     * the repeating OIDs. Agent's must truncate the list to something 
     * shorter if it won't fit within the max-message size supported by 
     * the command generator or the agent.
     */
    int getMaxRepetitions();

    /**
     * Sets the remote agent connection port.
     *
     * @param port on which to connect to the remote SNMP agent.
     */
    void setPort(int port);

    /**
     * Gets the remote agent connection port.
     *
     * @return port
     */
    int getPort();

    /**
     * @return the maximum number of columns to be retrieved per PDU, when executing a row-wise table walk.
     * See org.snmp4j.util.TableUtils.
     */
    int getMaximumColumnsPerPdu();

    /**
     * @return the maximum number of rows to be retrieved per PDU, when executing a row-wise table walk.
     * See org.snmp4j.util.TableUtils.
     */
    int getMaximumRowsPerPdu();
}