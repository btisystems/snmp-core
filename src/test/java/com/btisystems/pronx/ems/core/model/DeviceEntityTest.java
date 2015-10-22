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

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;

/**
 *
 * @author skoneru
 */
public class DeviceEntityTest {

    private DeviceEntityImpl deviceEntity;
    private DeviceEntityImpl device_1;
    PropertyChangeListener propertyChangeListenerMock;

    public DeviceEntityTest() {
    }

    @Before
    public void setUp() {

        deviceEntity = new DeviceEntityImpl("1");
        device_1 = new DeviceEntityImpl("2");
        propertyChangeListenerMock = createMock(PropertyChangeListener.class);
    }

    @After
    public void tearDown() {
        propertyChangeListenerMock = null;
        device_1 = null;
        deviceEntity = null;
    }

    @Test
    public void setString() {
        deviceEntity.set("deviceName", "testDevice");
        assertEquals("testDevice", deviceEntity.getString("deviceName"));
    }

    @Test
    public void setInt() {
        deviceEntity.set("deviceId", 123);
        assertEquals(123, deviceEntity.getInt("deviceId"));
    }

    @Test
    public void isSupported() {
        deviceEntity.set("deviceName", "testDevice");
        deviceEntity.get_Description().addField(new FieldDescription(123, "testDevice", DeviceEntityDescription.FieldType.STRING, -1));
        assertTrue(deviceEntity.isSupported("testDevice"));
    }

    @Test
    public void addPropertyChangeListener() throws Exception {
        prepareDeviceEntityFieldTypes();

            assertEquals(0, device_1._getChangeListeners().size());
            deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
            assertEquals(1, device_1._getChangeListeners().size());
    }

    @Test
    public void removePropertyChangeListener() throws Exception{
            prepareDeviceEntityFieldTypes();
            deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
            assertEquals(1, device_1._getChangeListeners().size());
            deviceEntity.removePropertyChangeListener(propertyChangeListenerMock);
            assertEquals(0, device_1._getChangeListeners().size());
    }

    @Test
    public void addChild() throws Exception{
            prepareDeviceEntityFieldTypes();
            assertEquals(0, device_1._getChangeListeners().size());
            deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
            deviceEntity.addChild(device_1);
            assertEquals(1, device_1._getChangeListeners().size());
    }

    @Test
    public void removeChild() throws Exception{
            prepareDeviceEntityFieldTypes();

            deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);

            assertEquals(1, device_1._getChangeListeners().size());

            deviceEntity.removeChild(device_1);

            assertEquals(0, device_1._getChangeListeners().size());
    }

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

    @Test
    public void getMacAddress() {
        final int[] intArray = {80, 00, 69, 02, 01, 33};

        assertEquals("50:00:45:02:01:21", deviceEntity._getMacAddress(intArray, 0, 6));
    }

    @Test
    public void getObjectIdentifier() {
        final int[] intArray = {80, 00, 69, 02, 01, 33};
        assertEquals("80.0.69.2.1.33", deviceEntity._getObjectIdentifier(intArray, 0, 6));
    }

    @Test
    public void Objectclone() {
        assertNull(deviceEntity.clone());

    }

    public void notifyChange() throws Exception{
            prepareDeviceEntityFieldTypes();

            deviceEntity.addPropertyChangeListener(propertyChangeListenerMock);
            deviceEntity.notifyChange(123, null, "testDevice");

    }

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