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

package com.btisystems.pronx.ems.core.model;

import com.btisystems.pronx.ems.core.model.testpackage1._OidRegistry;
import com.btisystems.pronx.ems.core.model.testpackage1.I_Device;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.smi.OID;


public class ClassRegistryTest {
    private ClassRegistry registry;
    private String thisPackageName;
    private String testPackageName;

    @Before
    public void setUp() throws Exception {
        registry = new ClassRegistry(_OidRegistry.oidRegistry, I_Device.class);
        thisPackageName = this.getClass().getPackage().getName();
        testPackageName = thisPackageName + ".testpackage1";
    }

    @Test
    public void shouldGetRootEntity()  {

        assertEquals(testPackageName + "." + GeneratedIdentifiers.ROOT_CLASS_NAME,
                     registry.getRootEntityClass().getCanonicalName());
    }

    @Test
    public void shouldGetClassByName()  {
        assertEquals(testPackageName + ".sub.EntityTest1",
                     registry.getClass("EntityTest1").getCanonicalName());
    }

    @Test
    public void shouldGetClassByOid()  {
        assertEquals(testPackageName + ".sub.EntityTest1",
                     registry.getClass(new OID("1.3.6.1.2.1.1")).getCanonicalName());
    }

    @Test
    public void shouldGetDiscoveryOids() throws InterruptedException  {
        registry.setExcludedDiscoveryOids("1.3.6.1.2.1.1.1");
        assertEquals(15, registry.getDiscoveryOids().size());
    }

    @Test
    public void shouldGetEntityDescription() throws InterruptedException  {
        final DeviceEntityDescription description = registry.getEntityDescription(new OID("1.3.6.1.2.1.1"));
        assertEquals(description.getOid(), new OID("1.3.6.1.2.1.1"));
    }

    @Test
    public void shouldGetContainingEntityClassWhenGivenTable() throws InterruptedException  {
        Class<? extends DeviceEntity> clazz = registry.getContainingEntityClass("1.3.6.1.2.2.3");
        assertEquals("TableEntityTest", clazz.getSimpleName());
    }

    @Test
    public void shouldGetContainingEntityClassWhenGivenTableEntry() throws InterruptedException  {
        Class<? extends DeviceEntity> clazz = registry.getContainingEntityClass("1.3.6.1.2.2.3.1");
        assertEquals("TableEntityTest", clazz.getSimpleName());
    }

    @Test
    public void shouldGetContainingEntityClassWhenGivenTableEntryField() throws InterruptedException  {
        Class<? extends DeviceEntity> clazz = registry.getContainingEntityClass("1.3.6.1.2.2.3.1.7");
        assertEquals("TableEntryTest", clazz.getSimpleName());
    }


    @Test
    public void shouldGetContainingEntityClassWhenGivenDeviceEntity() throws InterruptedException  {
        Class<? extends DeviceEntity> clazz = registry.getContainingEntityClass("1.3.6.1.2.1.2");
        assertEquals("EntityTest2", clazz.getSimpleName());
    }

    @Test
    public void shouldGetContainingEntityClassWhenGivenDeviceEntityMember() throws InterruptedException  {
        Class<? extends DeviceEntity> clazz = registry.getContainingEntityClass("1.3.6.1.2.1.2.100");
        assertEquals("EntityTest2", clazz.getSimpleName());
    }

}
