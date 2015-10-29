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

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SnmpConfigurationFactoryTest {
    @Mock private ISnmpConfiguration defaultConfig;
    @Mock private ISnmpConfiguration readConfig;
    @Mock private ISnmpConfiguration writeConfig;
    
    public SnmpConfigurationFactoryTest() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void shouldProvideDefaultConfig() {
        final ISnmpConfigurationFactory defaultFactory = new DefaultSnmpConfigurationFactory(defaultConfig);
        
        assertEquals(defaultConfig, defaultFactory.getConfiguration(ISnmpConfigurationFactory.AccessType.READ_ONLY));
        assertEquals(defaultConfig, defaultFactory.getConfiguration(ISnmpConfigurationFactory.AccessType.READ_WRITE));
    }
    
    @Test
    public void shouldProvideReadWriteConfig() {
        final ISnmpConfigurationFactory defaultFactory = new ReadWriteSnmpConfigurationFactory(readConfig, writeConfig);
        
        assertEquals(readConfig, defaultFactory.getConfiguration(ISnmpConfigurationFactory.AccessType.READ_ONLY));
        assertEquals(writeConfig, defaultFactory.getConfiguration(ISnmpConfigurationFactory.AccessType.READ_WRITE));
    }
    
    
}
