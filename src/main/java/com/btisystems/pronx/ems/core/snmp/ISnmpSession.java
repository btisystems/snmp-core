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

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for an snmp4j SNMP session, providing functionality to identify the
 * session device and walk the device's tree of managed objects.
 */
public interface ISnmpSession extends AutoCloseable {

    /**
     * Identify the device with which the session is associated
     *
     * @return string representing the device
     */
    String identifyDevice();

    /**
     * Deliver the string value of a specified managed object variable.
     *
     * @param oid the identifier of the variable to be returned
     * @return string representation of the variable value or <code>null</code>
     * if the variabled could not be retrieved
     */
    String getVariable(String oid);

    /**
     * Deliver the Integer value of a specified managed object variable.
     *
     * @param oid the identifier of the variable to be returned
     * @return int value or null if the variabled could not be retrieved
     */
    Integer getVariableAsInt(String oid);

    /**
     * Walk managed objects of a device.
     *
     * @param networkDevice an {@link IVariableBindingHandler} which will handle
     * the values retrieved from the device
     * @param oids ordered list of OIDs to be retrieved
     * @return a {@link WalkResponse} describing the completion status of the
     * walk
     * @throws IOException
     */
    WalkResponse walkDevice(final IVariableBindingHandler networkDevice, List<OID> oids) throws IOException;

    /**
     * Gets the contents of each of the rows with specified index values for a
     * set of tables.
     *
     * @param networkDevice an {@link IVariableBindingHandler} which will handle
     * the values retrieved from the device
     * @param tableIndexes map of the tables to be updated, each with a list of
     * indexes of the rows to be updated
     * @return a {@link WalkResponse} describing the completion status of the
     * retrieval
     * @throws IOException
     */
    WalkResponse getTableRows(final IVariableBindingHandler networkDevice, Map<DeviceEntityDescription, List<OID>> tableIndexes) throws IOException;

    /**
     * @return the Inet Address of the device associated with the session
     */
    InetAddress getAddress();

    /**
     * Close the session and release associated resources.
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException;

    /**
     * Sets the provided varbinds on the device.
     *
     * @param bindings
     */
    void setVariables(VariableBinding[] bindings);

    /**
     * Throws an exception providing the error code and/or description values
     * for the device.
     *
     * Some network devices provide a way of getting a more specific error code
     * and description after a failed 'set' operation on one or many OIDs. This
     * method provides a way for vendor specific SnmpSessions to be implemented,
     * with implementations for those custom error codes. This method should
     * throw a new {@link SnmpIoException} if code or description is set
     * providing any error code or description available which in turn gives a
     * better
     *
     * @throws SnmpIoException
     */
    void checkErrorCodeAndDescription() throws SnmpIoException;
}
