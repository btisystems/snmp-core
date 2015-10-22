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

    private static Logger log = LoggerFactory.getLogger(WalkResponse.class);

    private final boolean isSuccess;
    private final Throwable throwable;

    // Walk record
    private long walkTime;      // Time taken
    private long pingTime;      // ICMP latency
    private int objectCount;    // Number of bindings returned
    private int requestCount;   // Number of requests

    public WalkResponse(final boolean isSuccess) {
        log.debug(">>> DeviceDiscoveryResponse:{}", isSuccess);
        this.isSuccess = isSuccess;
        throwable = null;
    }

    public WalkResponse(final Throwable throwable) {
        this.isSuccess = false;
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return "success";
        } else {
            return "failed:" + ((throwable != null) ? throwable.getMessage() : "???");
        }
    }

    public long getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(final long walkTime) {
        this.walkTime = walkTime;
    }

    public long getPingTime() {
        return pingTime;
    }

    public void setPingTime(final long pingTime) {
        this.pingTime = pingTime;
    }

    public int getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(final int objectCount) {
        this.objectCount = objectCount;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(final int requestCount) {
        this.requestCount = requestCount;
    }
}