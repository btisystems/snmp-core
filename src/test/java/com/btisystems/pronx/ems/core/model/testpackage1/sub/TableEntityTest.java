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

package com.btisystems.pronx.ems.core.model.testpackage1.sub;

import java.io.Serializable;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.core.model.AbstractRootEntity;
import com.btisystems.pronx.ems.core.model.DeviceEntity;
import com.btisystems.pronx.ems.core.model.IDeviceEntity;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import com.btisystems.pronx.ems.core.model.ITableAccess;

public class TableEntityTest
    extends DeviceEntity
    implements Serializable, ITableAccess
{
    
    private int _id;
    private AbstractRootEntity parentEntity;

    private final static DeviceEntityDescription _entityDescription = createEntityDescription();

    public TableEntityTest() {
    }

    @Override
    public void set_ParentEntity(final AbstractRootEntity parent) {
        parentEntity = parent;
    }


    private static DeviceEntityDescription createEntityDescription() {
        final DeviceEntityDescription description = new DeviceEntityDescription(new OID("1.3.6.1.2.1.1"));
        description.addField(new DeviceEntityDescription.FieldDescription(1, "macaddress", DeviceEntityDescription.FieldType.STRING, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(2, "configfile", DeviceEntityDescription.FieldType.INTEGER, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(3, "lfpcontrol", DeviceEntityDescription.FieldType.INTEGER, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(4, "systemtime", DeviceEntityDescription.FieldType.INTEGER, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(5, "jamborframe", DeviceEntityDescription.FieldType.INTEGER, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(6, "reboot", DeviceEntityDescription.FieldType.INTEGER, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(7, "softwareVersion", DeviceEntityDescription.FieldType.STRING, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(8, "hardwareVersion", DeviceEntityDescription.FieldType.STRING, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(9, "qinqForwardVID", DeviceEntityDescription.FieldType.INTEGER, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(10, "snmpTrapSer1", DeviceEntityDescription.FieldType.STRING, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(11, "snmpTrapSer2", DeviceEntityDescription.FieldType.STRING, -1));
        description.addField(new DeviceEntityDescription.FieldDescription(12, "qinqEtherType", DeviceEntityDescription.FieldType.INTEGER, -1));
        return description;
    }

    @Override
    public DeviceEntityDescription get_Description() {
        return _entityDescription;
    }

    @Override
    public IDeviceEntity getEntry(String index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEntry(String index, IDeviceEntity entry) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map getEntries() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDeviceEntity createEntry(String index) {
        // TODO Auto-generated method stub
        return null;
    }

}
