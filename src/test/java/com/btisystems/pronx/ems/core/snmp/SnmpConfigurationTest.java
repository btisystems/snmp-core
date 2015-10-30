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
package com.btisystems.pronx.ems.core.snmp;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SnmpConfigurationTest {
    
    public SnmpConfigurationTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void shouldContainBeanMethods() {
        assertThat(V2cSnmpConfiguration.class, 
            allOf(
                hasValidBeanConstructor(), 
                hasValidGettersAndSetters(), 
                hasValidBeanToString()
            )
        );
    }
    
}
