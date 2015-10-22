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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates the parameters describing the response to an SNMP walk request.
 */
public class WalkResponse {

    /**
     * The constant SUCCESS.
     */
    public static final String SUCCESS = "success";
    /**
     * The constant FAILED.
     */
    public static final String FAILED = "failed:";
    /**
     * The constant NO_IDEA.
     */
    public static final String NO_IDEA = "???";
    private static Logger log = LoggerFactory.getLogger(WalkResponse.class);

    private final boolean isSuccess;
    private final Throwable throwable;

    // Walk record
    private long walkTime;      // Time taken
    private long pingTime;      // ICMP latency
    private int objectCount;    // Number of bindings returned
    private int requestCount;   // Number of requests

    /**
     * Instantiates a new Walk response.
     *
     * @param isSuccess the is success
     */
    public WalkResponse(final boolean isSuccess) {
        log.debug(">>> DeviceDiscoveryResponse:{}", isSuccess);
        this.isSuccess = isSuccess;
        throwable = null;
    }

    /**
     * Instantiates a new Walk response.
     *
     * @param throwable the throwable
     */
    public WalkResponse(final Throwable throwable) {
        this.isSuccess = false;
        this.throwable = throwable;
    }

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Gets throwable.
     *
     * @return the throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return SUCCESS;
        } else {
            return FAILED + ((throwable != null) ? throwable.getMessage() : NO_IDEA);
        }
    }

    /**
     * Gets walk time.
     *
     * @return the walk time
     */
    public long getWalkTime() {
        return walkTime;
    }

    /**
     * Sets walk time.
     *
     * @param walkTime the walk time
     */
    public void setWalkTime(final long walkTime) {
        this.walkTime = walkTime;
    }

    /**
     * Gets ping time.
     *
     * @return the ping time
     */
    public long getPingTime() {
        return pingTime;
    }

    /**
     * Sets ping time.
     *
     * @param pingTime the ping time
     */
    public void setPingTime(final long pingTime) {
        this.pingTime = pingTime;
    }

    /**
     * Gets object count.
     *
     * @return the object count
     */
    public int getObjectCount() {
        return objectCount;
    }

    /**
     * Sets object count.
     *
     * @param objectCount the object count
     */
    public void setObjectCount(final int objectCount) {
        this.objectCount = objectCount;
    }

    /**
     * Gets request count.
     *
     * @return the request count
     */
    public int getRequestCount() {
        return requestCount;
    }

    /**
     * Sets request count.
     *
     * @param requestCount the request count
     */
    public void setRequestCount(final int requestCount) {
        this.requestCount = requestCount;
    }
}