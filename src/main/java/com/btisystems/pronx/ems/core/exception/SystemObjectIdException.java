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
package com.btisystems.pronx.ems.core.exception;

import org.snmp4j.smi.Address;
import org.snmp4j.smi.IpAddress;


/**
 * Thrown when the system object id of a device cannot be retrieved
 */
public class SystemObjectIdException extends DetailedFaultException {

    private static final long serialVersionUID = -2285401779827208734L;

    /**
     * Instantiates a new System object id exception.
     *
     * @param address the address
     */
    public SystemObjectIdException(final Address address) {
        super("Failed to get System Object Id from device [%1$s]", getHostAddress(address));
    }

    /**
     * Instantiates a new System object id exception.
     *
     * @param address   the address
     * @param throwable the throwable
     */
    public SystemObjectIdException(final Address address,
                                   final Throwable throwable) {
        super("Failed to get System Object Id from device [%1$s] [%2$s]", getHostAddress(address), throwable.getMessage());
    }

    private static String getHostAddress(final Address address) {
        return ((IpAddress) address).getInetAddress().getHostAddress();
    }
}
