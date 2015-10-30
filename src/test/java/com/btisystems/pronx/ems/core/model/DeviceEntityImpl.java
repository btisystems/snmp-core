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

import org.snmp4j.smi.OID;

import java.util.HashMap;
import java.util.Map;

/**
 * Exclusively used for Testing DeviceEntity abstract class.
 * <p/>
 * All child devices are manipulated (add,remove or replace etc) using propertyChangeListener.
 */
public class DeviceEntityImpl extends DeviceEntity implements ITableAccess {

    private String deviceName;
    private String tableDeviceName;
    private DeviceEntity entityObject;
    private int deviceId;
    private long time;
    private DeviceEntityDescription deviceEntityDescription;
    private Map<String, IDeviceEntity> entityMap;

    /**
     * Instantiates a new Device entity.
     *
     * @param oId the o id
     */
    public DeviceEntityImpl(String oId) {
        OID oid = new OID(oId);
        deviceEntityDescription = new DeviceEntityDescription(oid);
        entityMap = new HashMap<String, IDeviceEntity>();
    }

    /**
     * Gets entity object.
     *
     * @return the entity object
     */
    public DeviceEntity getEntityObject() {
        return entityObject;
    }

    /**
     * Sets entity object.
     *
     * @param entityObject the entity object
     */
    public void setEntityObject(DeviceEntity entityObject) {
        this.entityObject = entityObject;
    }

    /**
     * Gets table device name.
     *
     * @return the table device name
     */
    public String getTableDeviceName() {
        return tableDeviceName;
    }

    /**
     * Sets table device name.
     *
     * @param tableDeviceName the table device name
     */
    public void setTableDeviceName(String tableDeviceName) {
        this.tableDeviceName = tableDeviceName;
    }

    /**
     * Gets device id.
     *
     * @return the device id
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * Sets device id.
     *
     * @param deviceId the device id
     */
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    
    /**
     * Gets device name.
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets device name.
     *
     * @param deviceName the device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public DeviceEntityDescription get_Description() {
        return deviceEntityDescription;
    }

    @Override
    public IDeviceEntity getEntry(String index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEntry(String index, IDeviceEntity entry) {
        entityMap.put(index, entry);
    }

    @Override
    public Map getEntries() {
        return entityMap;
    }

    @Override
    public IDeviceEntity createEntry(String index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
