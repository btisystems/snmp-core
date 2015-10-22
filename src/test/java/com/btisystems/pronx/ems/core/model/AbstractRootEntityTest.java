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

import com.btisystems.pronx.ems.core.exception.InvalidFieldNameException;
import org.junit.Before;
import org.junit.Test;
import org.snmp4j.smi.OID;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class AbstractRootEntityTest {
    private static final String DEVICE_ENTITY_CLASSNAME  = "MyDeviceEntity";
    private static final String DUMMY_GETTER_METHOD_NAME = "dummy";
    private static final String OID_TO_SEARCH_FOR        = "100.0";
    private static final int    NUMBER_OF_ROOT_ENTITIES  = 3;

    private   DeviceEntity[] deviceEntity                = new DeviceEntity[NUMBER_OF_ROOT_ENTITIES];

    private   boolean wantNullRootDescription            = false;
    private   boolean wantNullRoot                       = false;

    protected boolean setterMethodWasCalled              = false;
    protected boolean getterMethodWasCalled              = false;


    @Before
    public void setUp() {
        setterMethodWasCalled = false;
        getterMethodWasCalled = false;
    }

    @Test
    public void verifyGetEntityWithOid_expectNullRoot() {
        wantNullRoot = true;
        RootEntity rootEntity = new RootEntity();
        assertNull(rootEntity.getEntity((OID)null));
    }

    @Test
    public void verifyGetEntityWithOid_expectNullDescription() {
        wantNullRootDescription = true;
        RootEntity rootEntity = new RootEntity();
        assertNull(rootEntity.getEntity((OID)null));
        verifyMocks();
    }

    @Test
    public void verifyGetEntityWithOid_expectSuccess() {
        RootEntity rootEntity = new RootEntity();
        assertNotNull(rootEntity.getEntity(new OID(OID_TO_SEARCH_FOR)));
        verifyMocks();
    }

    @Test
    public void verifyIsEntitySupported_expectFalse() {
        RootEntity rootEntity = new RootEntity();
        boolean result = rootEntity.isEntitySupported(DUMMY_GETTER_METHOD_NAME);
        assertFalse(result);
    }

    @Test
    public void verifyIsEntitySupported_expectTrue() {
        RootEntity rootEntity = new RootEntity();
        boolean result = rootEntity.isEntitySupported(DEVICE_ENTITY_CLASSNAME);
        assertTrue(result);
    }

    @Test
    public void verifyGetEntityWithFieldName_expectException() {
        RootEntity rootEntity = new RootEntity();
        boolean caughtExpectedException = false;

        try {
            rootEntity.getEntity(DUMMY_GETTER_METHOD_NAME);
        } catch (InvalidFieldNameException e) {
            caughtExpectedException = true;
        } catch (Exception e) {
            caughtExpectedException = false;
        }

        assertTrue(caughtExpectedException);
    }

    @Test
    public void verifyGetEntityWithFieldName_expectSuccess() {
        RootEntity rootEntity = new RootEntity();
        Object childObject = rootEntity.getEntity(DEVICE_ENTITY_CLASSNAME);
        assertNull(childObject);
    }

    @Test
    public void verifySetObject_expectException() {
        RootEntity rootEntity = new RootEntity();
        boolean caughtExpectedException = false;

        try {
            rootEntity.setObject(RootEntity.class, null);
        } catch (InvalidFieldNameException e) {
            caughtExpectedException = true;
        } catch (Exception e) {
            caughtExpectedException = false;
        }

        assertTrue(caughtExpectedException);
        assertFalse(setterMethodWasCalled);
    }

    @Test
    public void verifySetObject_expectSuccess() {
        RootEntity rootEntity = new RootEntity();
        rootEntity.setObject(MyDeviceEntity.class, null);
        assertTrue(setterMethodWasCalled);
    }

    @Test
    public void verifyGetObject_expectException() {
        RootEntity rootEntity = new RootEntity();
        boolean caughtExpectedException = false;

        try {
            rootEntity.getObject(RootEntity.class);
        } catch (InvalidFieldNameException e) {
            caughtExpectedException = true;
        } catch (Exception e) {
            caughtExpectedException = false;
        }

        assertTrue(caughtExpectedException);
        assertFalse(getterMethodWasCalled);
    }

    @Test
    public void verifyGetObject_expectSuccess() {
        RootEntity rootEntity = new RootEntity();
        Object childObject = rootEntity.getObject(MyDeviceEntity.class);
        assertNull(childObject);
        assertTrue(getterMethodWasCalled);
    }

    @Test
    public void shouldFailToCreateDeviceEntity() {
        RootEntity rootEntity = new RootEntity();
        boolean caughtExpectedException = false;

        try {
            rootEntity.createEntity(DUMMY_GETTER_METHOD_NAME);
        } catch (InvalidFieldNameException e) {
            caughtExpectedException = true;
        } catch (Exception e) {
            caughtExpectedException = false;
        }

        assertTrue(caughtExpectedException);
    }

    @Test
    public void shouldCreateDeviceEntity() {
        RootEntity rootEntity = new RootEntity();
        DeviceEntity deviceEntity = rootEntity.createEntity(DEVICE_ENTITY_CLASSNAME);
        assertNotNull(deviceEntity);
        assertTrue(deviceEntity instanceof MyDeviceEntity);
    }

    @Test
    public void verifyGetDescription() {
        RootEntity rootEntity = new RootEntity();
        assertNull(rootEntity.get_Description());
    }


    private void verifyMocks() {
        for (int i=0; i < NUMBER_OF_ROOT_ENTITIES; i++) {
            if (null != deviceEntity[i]) {
                verify(deviceEntity[i]);
            }
        }
    }

    public class RootEntity extends AbstractRootEntity {

        public MyDeviceEntity getMyDeviceEntity() {
            getterMethodWasCalled = true;
            return null;
        }
        public void setMyDeviceEntity(MyDeviceEntity deviceEntity) {
            setterMethodWasCalled = true;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void setObject(final Class clazz, final Object childObject) {
            super.setObject(clazz, childObject);
        }

        @Override
        public DeviceEntity[] getRoots() {
            if (wantNullRoot) {
                return deviceEntity;
            }

            for (int i=0; i < NUMBER_OF_ROOT_ENTITIES; i++) {
                String oidValue = (new Double(Math.pow(10.0, i))).toString();  //  will be the sequence ["1.0", "10.0", "100.0"].
                OID oid = new OID(oidValue);

                int numberOfExpectedCalls = wantNullRootDescription ? 1 : 2;
                DeviceEntity mockDeviceEntity = createMock(DeviceEntity.class);
                DeviceEntityDescription description = wantNullRootDescription ? null : new DeviceEntityDescription(oid);

                expect(mockDeviceEntity.get_Description()).andReturn(description).times(numberOfExpectedCalls);
                replay(mockDeviceEntity);

                deviceEntity[i] = mockDeviceEntity;
            }

            return deviceEntity;
        }
    }
}
