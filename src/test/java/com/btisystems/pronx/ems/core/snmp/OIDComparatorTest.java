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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The type Var bind comparator test.
 */
public class OIDComparatorTest {
    private OIDComparator comparator;

    /**
     * Instantiates a new Var bind comparator test.
     */
    public OIDComparatorTest() {
    }

    /**
     * Sets up.
     */
    @Before
    public void setUp() {
        comparator = new OIDComparator();
    }

    /**
     * Should order var binds.
     */
    @Test
    public void shouldOrderSimpleVarBinds() {
        assertEquals(1, comparator.compare("1.1.1.2.1", "1.1.1.1.1"));
        assertEquals(0, comparator.compare("1.1.1.1.1", "1.1.1.1.1"));
        assertEquals(-1, comparator.compare("1.1.1.1.1", "1.1.1.2.1"));
    }
    
    @Test
    public void shouldPlaceLongerButSmallerLast() {
        //Example where the longer OID should actually be placed before the shorter.
        assertEquals(-1, comparator.compare("1.3.6.1.4.1.2020.1.2000.5.6.1", "1.3.6.1.4.1.2020.2.2008.1.9"));
        //Longer OIDs should appear lower if length is the only difference.
        assertEquals(-2, comparator.compare("1.1.1.1", "1.1.1.1.1"));
    }
    
}
