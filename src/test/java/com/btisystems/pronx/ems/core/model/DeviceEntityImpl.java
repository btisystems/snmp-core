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

import java.util.HashMap;
import java.util.Map;

import com.btisystems.pronx.ems.core.model.DeviceEntity;
import com.btisystems.pronx.ems.core.model.IDeviceEntity;
import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import com.btisystems.pronx.ems.core.model.ITableAccess;

/**
 * Exclusivley used for Testing DeviceEntity abstract class.
 *
 * All child devices are manipulated (add,remove or replace etc) using propertyChangeListener.
 *
 * @author skoneru
 */
public class DeviceEntityImpl extends DeviceEntity implements ITableAccess {

    private String deviceName;
    private String tableDeviceName;
    private DeviceEntity entityObject;
    private int deviceId;
    private DeviceEntityDescription deviceEntityDescription;
    private Map<String, IDeviceEntity> entityMap;

    public DeviceEntityImpl(String oId) {
        OID oid = new OID(oId);
        deviceEntityDescription = new DeviceEntityDescription(oid);
        entityMap = new HashMap<String, IDeviceEntity>();
    }

    public DeviceEntity getEntityObject() {
        return entityObject;
    }

    public void setEntityObject(DeviceEntity entityObject) {
        this.entityObject = entityObject;
    }

    public String getTableDeviceName() {
        return tableDeviceName;
    }

    public void setTableDeviceName(String tableDeviceName) {
        this.tableDeviceName = tableDeviceName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

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
