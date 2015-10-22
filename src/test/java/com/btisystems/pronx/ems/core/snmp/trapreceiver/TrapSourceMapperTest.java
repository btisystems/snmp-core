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
package com.btisystems.pronx.ems.core.snmp.trapreceiver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Trap source mapper test.
 */
public class TrapSourceMapperTest {

    /**
     * Should map to single address.
     */
    @Test
    public void shouldMapToSingleAddress() {

        TrapSourceMapper mapper = new TrapSourceMapper("127.0.0.1:192.0.0.101");

        assertEquals("192.0.0.101", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.101", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.102", mapper.mapAddress("192.0.0.102"));
    }

    /**
     * Should map to multiple explicit addresses.
     */
    @Test
    public void shouldMapToMultipleExplicitAddresses() {

        TrapSourceMapper mapper = new TrapSourceMapper("127.0.0.1:192.0.0.101" +
                ",192.0.0.102" +
                ",192.0.0.103");

        assertEquals("192.0.0.101", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.102", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.103", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.101", mapper.mapAddress("127.0.0.1"));
    }

    /**
     * Should map to a range of addresses.
     */
    @Test
    public void shouldMapToARangeOfAddresses() {

        TrapSourceMapper mapper = new TrapSourceMapper("127.0.0.1:192.0.0.201-203");

        assertEquals("192.0.0.201", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.202", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.203", mapper.mapAddress("127.0.0.1"));
        assertEquals("192.0.0.201", mapper.mapAddress("127.0.0.1"));
    }
}
