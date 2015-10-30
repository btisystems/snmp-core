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

import com.btisystems.pronx.ems.core.exception.FieldAccessMethodException;
import com.btisystems.pronx.ems.core.exception.InvalidFieldNameException;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import java.beans.PropertyChangeEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * The type Device entity test.
 */
public class DeviceEntityTest {

    private DeviceEntityImpl deviceEntity;
    private DeviceEntityImpl device_1;
    
    boolean propertyChanged;
    /**
     * The Property change listener mock.
     */
    PropertyChangeListener propertyChangeListenerMock;

    /**
     * Instantiates a new Device entity test.
     */
    public DeviceEntityTest() {
    }

    /**
     * Sets up.
     */
    @Before
    public void setUp() {

        deviceEntity = new DeviceEntityImpl("1");
        device_1 = new DeviceEntityImpl("2");
        propertyChangeListenerMock = createMock(PropertyChangeListener.class);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        propertyChangeListenerMock = null;
        device_1 = null;
        deviceEntity = null;
    }

    /**
     * Sets string.
     */
    @Test
    public void setString() {
        deviceEntity.set("deviceName", "testDevice");
        assertEquals("testDevice", deviceEntity.getString("deviceName"));
    }
    
    @Test(expected = InvalidFieldNameException.class)
    public void shouldCatchNoSuchMethodExceptions() {
        deviceEntity.set("not a field", "testDevice");
    }
    
    @Test(expected = FieldAccessMethodException.class)
    public void shouldCatchExceptions() throws Exception {
        deviceEntity = new DeviceEntityImpl("1"){
            @Override
            protected String getSetterName(final String field) throws Exception {
                throw new IOException("Some other exception");
            } 
        };
        deviceEntity.set("deviceName", "testDevice");
    }
    
    @Test(expected = InvalidFieldNameException.class)
    public void shouldCatchUnsuppoeredOperationOnGet() {
        deviceEntity.getInt("not a field");
    }

    /**
     * Sets int.
     */
    @Test
    public void setInt() {
        deviceEntity.set("deviceId", 123);
        assertEquals(123, deviceEntity.getInt("deviceId"));
    }
    
    @Test
    public void setLong() {
        deviceEntity.set("time", 123L);
        assertEquals(123, deviceEntity.getLong("time"));
    }
    
    @Test
    public void shouldSupportToString() {
        assertNotNull(deviceEntity.toString());
    }
    
    @Test
    public void shouldNotifyChange() {

        deviceEntity.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                assertEquals("Old", evt.getOldValue());
                assertEquals("New", evt.getNewValue());
                assertEquals("1", evt.getPropertyName());
                propertyChanged = true;
            }
        });
        deviceEntity.notifyChange(1, "Old", "New");
        assertTrue(propertyChanged);
        propertyChanged = false;
        
        deviceEntity.clearPropertyChangeListeners();
        deviceEntity.notifyChange(1, "Old", "New");
        assertFalse(propertyChanged);
    }
    

    /**
     * Is supported.
     */
    @Test
    public void isSupported() {
        deviceEntity.set("deviceName", "testDevice");
        deviceEntity.get_Description().addField(new FieldDescription(123, "testDevice", DeviceEntityDescription.FieldType.STRING, -1));
        assertTrue(deviceEntity.isSupported("testDevice"));
    }

    /**
     * Add property change listener.
     *
     * @throws Exception the exception
     */
    @Test
    public void addPropertyChangeListener() throws Exception {
        prepareDeviceEntityFieldTypes();

        assertEquals(0, device_1._getChangeListeners().size());
        deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
        assertEquals(1, device_1._getChangeListeners().size());
    }

    /**
     * Remove property change listener.
     *
     * @throws Exception the exception
     */
    @Test
    public void removePropertyChangeListener() throws Exception {
        prepareDeviceEntityFieldTypes();
        deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
        assertEquals(1, device_1._getChangeListeners().size());
        deviceEntity.removePropertyChangeListener(propertyChangeListenerMock);
        assertEquals(0, device_1._getChangeListeners().size());
    }

    /**
     * Add child.
     *
     * @throws Exception the exception
     */
    @Test
    public void addChild() throws Exception {
        prepareDeviceEntityFieldTypes();
        assertEquals(0, device_1._getChangeListeners().size());
        deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
        deviceEntity.addChild(device_1);
        assertEquals(1, device_1._getChangeListeners().size());
    }

    /**
     * Remove child.
     *
     * @throws Exception the exception
     */
    @Test
    public void removeChild() throws Exception {
        prepareDeviceEntityFieldTypes();

        deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);

        assertEquals(1, device_1._getChangeListeners().size());

        deviceEntity.removeChild(device_1);

        assertEquals(0, device_1._getChangeListeners().size());
    }

    /**
     * Replace child.
     */
    @Test
    public void replaceChild() {
        final DeviceEntityImpl device_2 = new DeviceEntityImpl("4");

        deviceEntity.get_Description().addField(new FieldDescription(234, "entityObject", DeviceEntityDescription.FieldType.ENTITY, -1));
        deviceEntity.setEntityObject(device_1);
        deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
        deviceEntity.replaceChild(device_1, device_2);

        assertEquals(0, device_1._getChangeListeners().size());
        assertEquals(1, device_2._getChangeListeners().size());
    }

    /**
     * Gets mac address.
     */
    @Test
    public void getMacAddress() {
        final int[] intArray = {80, 00, 69, 02, 01, 33};

        assertEquals("50:00:45:02:01:21", deviceEntity._getMacAddress(intArray, 0, 6));
    }

    /**
     * Gets object identifier.
     */
    @Test
    public void getObjectIdentifier() {
        final int[] intArray = {80, 00, 69, 02, 01, 33};
        assertEquals("80.0.69.2.1.33", deviceEntity._getObjectIdentifier(intArray, 0, 6));
    }

    /**
     * Objectclone.
     */
    @Test
    public void Objectclone() {
        assertNull(deviceEntity.clone());

    }

    /**
     * Notify change.
     *
     * @throws Exception the exception
     */
    public void notifyChange() throws Exception {
        prepareDeviceEntityFieldTypes();

        deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
        deviceEntity.notifyChange(123, null, "testDevice");

    }

    /**
     * Prepare device entity field types device entity.
     *
     * @return the device entity
     */
    public DeviceEntity prepareDeviceEntityFieldTypes() {
        device_1.setDeviceName("testDevice_1");
        device_1.setDeviceId(123);
        deviceEntity.setEntry("1", new DeviceEntityImpl("2"));
        deviceEntity.setEntry("2", new DeviceEntityImpl("3"));
        deviceEntity.get_Description().addField(new FieldDescription(123, "deviceName", DeviceEntityDescription.FieldType.STRING, -1));
        deviceEntity.get_Description().addField(new FieldDescription(234, "entityObject", DeviceEntityDescription.FieldType.ENTITY, -1));
        deviceEntity.setEntityObject(device_1);
        deviceEntity.get_Description().addField(new FieldDescription(345, "tableDeviceName", DeviceEntityDescription.FieldType.TABLE, -1));

        return deviceEntity;
    }
}
