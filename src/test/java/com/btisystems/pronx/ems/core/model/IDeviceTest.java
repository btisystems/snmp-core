/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.btisystems.pronx.ems.core.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class IDeviceTest {
    private I_Device device;
    
    public IDeviceTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void shouldProvideDefaultImplementations() {
        device = new I_Device();
        assertEquals("", device.toString());
        assertFalse(device == device.clone());
        assertEquals(0, device.hashCode());
        assertTrue(device.equals(null));
        assertEquals(0, device.getRoots().length);
    }
    
}
