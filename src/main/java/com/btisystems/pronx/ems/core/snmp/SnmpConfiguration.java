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
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;

/**
 * Base class for SNMP Configuration, extended for different versions.
 */
public abstract class SnmpConfiguration implements ISnmpConfiguration {

    /**
     * The Version.
     */
    protected int version;
    
    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int DEFAULT_REPETITIONS = 10;
    private static final int DEFAULT_MAX_SIZE_RESPONSE_PDU = 65535;
    private static final int DEFAULT_WALK_TIMEOUT = 900000;
    private static final int DEFAULT_PORT = 161;
    private static final int DEFAULT_DISPATCHER_POOL_SIZE = 8;
    private static final int DEFAULT_MAX_ROWS_PER_PDU = 0; // 0 => use snmp4j default
    private static final int DEFAULT_MAX_COLUMNS_PER_PDU = 0; // 0 => use snmp4j default

    private int retries = 1;
    private int timeout = DEFAULT_TIMEOUT;
    private int maxRepetitions = DEFAULT_REPETITIONS;
    private int nonRepeaters = 0;
    private int maxSizeResponsePDU = DEFAULT_MAX_SIZE_RESPONSE_PDU;
    private int walkTimeout = DEFAULT_WALK_TIMEOUT;
    private int port = DEFAULT_PORT;
    private int dispatcherPoolSize = DEFAULT_DISPATCHER_POOL_SIZE;
    private int maximumRowsPerPdu = DEFAULT_MAX_ROWS_PER_PDU;
    private int maximumColumnsPerPdu = DEFAULT_MAX_COLUMNS_PER_PDU;

    private OctetString community;

    @Override
    public void setCommunity(final String community) {
        this.community = new OctetString(community);
    }

    /**
     * Sets retries.
     *
     * @param retries the retries
     */
    public void setRetries(final int retries) {
        this.retries = retries;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets max repetitions.
     *
     * @param maxRepetitions the max repetitions
     */
    public void setMaxRepetitions(final int maxRepetitions) {
        this.maxRepetitions = maxRepetitions;
    }

    /**
     * Sets non repeaters.
     *
     * @param nonRepeaters the non repeaters
     */
    public void setNonRepeaters(final int nonRepeaters) {
        this.nonRepeaters = nonRepeaters;
    }

    /**
     * Sets max size response pdu.
     *
     * @param maxSizeResponsePDU the max size response pdu
     */
    public void setMaxSizeResponsePDU(final int maxSizeResponsePDU) {
        this.maxSizeResponsePDU = maxSizeResponsePDU;
    }

    /**
     * Sets walk timeout.
     *
     * @param walkTimeout the walk timeout
     */
    public void setWalkTimeout(final int walkTimeout) {
        this.walkTimeout = walkTimeout;
    }

    /**
     * Gets dispatcher pool size.
     *
     * @return the dispatcher pool size
     */
    public int getDispatcherPoolSize() {
        return dispatcherPoolSize;
    }

    /**
     * Sets dispatcher pool size.
     *
     * @param dispatcherPoolSize the dispatcher pool size
     */
    public void setDispatcherPoolSize(final int dispatcherPoolSize) {
        this.dispatcherPoolSize = dispatcherPoolSize;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getWalkTimeout() {
        return walkTimeout;
    }

    /**
     * Gets retries.
     *
     * @return the retries
     */
    protected int getRetries() {
        return retries;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    protected int getTimeout() {
        return timeout;
    }
    @Override
    public int getMaxRepetitions() {
        return maxRepetitions;
    }

    /**
     * Gets non repeaters.
     *
     * @return the non repeaters
     */
    public int getNonRepeaters() {
        return nonRepeaters;
    }

    /**
     * Gets max size response pdu.
     *
     * @return the max size response pdu
     */
    public int getMaxSizeResponsePDU() {
        return maxSizeResponsePDU;
    }

    @Override
    public String getCommunity() {
        return community.toString();
    }
    @Override
    public int getMaximumRowsPerPdu() {
        return maximumRowsPerPdu;
    }

    /**
     * Sets maximum rows per pdu.
     *
     * @param maximumRowsPerPdu the maximum rows per pdu
     */
    public void setMaximumRowsPerPdu(final int maximumRowsPerPdu) {
		this.maximumRowsPerPdu = maximumRowsPerPdu;
    }
    @Override
    public int getMaximumColumnsPerPdu() {
        return maximumColumnsPerPdu;
    }

    /**
     * Sets maximum columns per pdu.
     *
     * @param maximumColumnsPerPdu the maximum columns per pdu
     */
    public void setMaximumColumnsPerPdu(final int maximumColumnsPerPdu) {
        this.maximumColumnsPerPdu = maximumColumnsPerPdu;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(final int port) {
        this.port = port;
    }
    
    /**
     * Deliver a {@link Target} built from the configuration
     *
     * @param address   the address of the device with which the target is to be associated
     * @return  a {@link Target} from the configuration
     */
    @Override
    public abstract Target createTarget(final Address address);

    /**
     * Deliver a {@link PDU} built from the configuration
     *
     * @param type  the request type
     * @return  a {@link PDU}, built from the configuration with the specified request type
     */
    @Override
    public abstract PDU createPDU(final int type);

    /**
     * Deliver an {@link Snmp} session for the specified transport mapping
     *
     * @param transportMapping  the initial transport mapping
     * @return an {@link Snmp} instance
     * @throws IOException
     */
    @Override
    public abstract Snmp createSnmpSession(final TransportMapping transportMapping) throws IOException;

    
	@Override
	public String toString() {
		//Eclipse generated toString.
		return "SnmpConfiguration [version=" + version + ", retries=" + retries + ", timeout=" + timeout
				+ ", maxRepetitions=" + maxRepetitions + ", nonRepeaters=" + nonRepeaters + ", maxSizeResponsePDU="
				+ maxSizeResponsePDU + ", walkTimeout=" + walkTimeout + ", port=" + port + ", dispatcherPoolSize="
				+ dispatcherPoolSize + ", maximumRowsPerPdu=" + maximumRowsPerPdu + ", maximumColumnsPerPdu="
				+ maximumColumnsPerPdu + ", community=" + community + "]";
	}



    
    
}