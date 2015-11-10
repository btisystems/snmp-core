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


import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.btisystems.pronx.ems.core.model.AbstractRootEntity;
import com.btisystems.pronx.ems.core.model.DeviceEntity;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import com.btisystems.pronx.ems.core.model.IVariableBindingSetter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EntityTest1
    extends DeviceEntity
    implements Serializable, IVariableBindingSetter
{

    private int _id;
    private AbstractRootEntity parentEntity;
    private String macaddress;
    private int configfile;
    private int lfpcontrol;
    private int systemtime;
    private int jamborframe;
    private int reboot;
    private String softwareVersion;
    private String hardwareVersion;
    private int qinqForwardVID;
    private String snmpTrapSer1;
    private String snmpTrapSer2;
    private int qinqEtherType;
    private final static DeviceEntityDescription _entityDescription = createEntityDescription();

    public EntityTest1() {
    }

    @Override
    public void set_ParentEntity(final AbstractRootEntity parent) {
        parentEntity = parent;
    }

    public String getMacaddress() {
        return this.macaddress;
    }

    public void setMacaddress(final String macaddress) {
        final String oldValue = this.macaddress;
        this.macaddress = macaddress;
        notifyChange(1, oldValue, macaddress);
    }

    public int getConfigfile() {
        return this.configfile;
    }

    public void setConfigfile(final int configfile) {
        final int oldValue = this.configfile;
        this.configfile = configfile;
        notifyChange(2, oldValue, configfile);
    }

    public int getLfpcontrol() {
        return this.lfpcontrol;
    }

    public void setLfpcontrol(final int lfpcontrol) {
        final int oldValue = this.lfpcontrol;
        this.lfpcontrol = lfpcontrol;
        notifyChange(3, oldValue, lfpcontrol);
    }

    public int getSystemtime() {
        return this.systemtime;
    }

    public void setSystemtime(final int systemtime) {
        final int oldValue = this.systemtime;
        this.systemtime = systemtime;
        notifyChange(4, oldValue, systemtime);
    }

    public int getJamborframe() {
        return this.jamborframe;
    }

    public void setJamborframe(final int jamborframe) {
        final int oldValue = this.jamborframe;
        this.jamborframe = jamborframe;
        notifyChange(5, oldValue, jamborframe);
    }

    public int getReboot() {
        return this.reboot;
    }

    public void setReboot(final int reboot) {
        final int oldValue = this.reboot;
        this.reboot = reboot;
        notifyChange(6, oldValue, reboot);
    }

    public String getSoftwareVersion() {
        return this.softwareVersion;
    }

    public void setSoftwareVersion(final String softwareVersion) {
        final String oldValue = this.softwareVersion;
        this.softwareVersion = softwareVersion;
        notifyChange(7, oldValue, softwareVersion);
    }

    public String getHardwareVersion() {
        return this.hardwareVersion;
    }

    public void setHardwareVersion(final String hardwareVersion) {
        final String oldValue = this.hardwareVersion;
        this.hardwareVersion = hardwareVersion;
        notifyChange(8, oldValue, hardwareVersion);
    }

    public int getQinqForwardVID() {
        return this.qinqForwardVID;
    }

    public void setQinqForwardVID(final int qinqForwardVID) {
        final int oldValue = this.qinqForwardVID;
        this.qinqForwardVID = qinqForwardVID;
        notifyChange(9, oldValue, qinqForwardVID);
    }

    public String getSnmpTrapSer1() {
        return this.snmpTrapSer1;
    }

    public void setSnmpTrapSer1(final String snmpTrapSer1) {
        final String oldValue = this.snmpTrapSer1;
        this.snmpTrapSer1 = snmpTrapSer1;
        notifyChange(10, oldValue, snmpTrapSer1);
    }

    public String getSnmpTrapSer2() {
        return this.snmpTrapSer2;
    }

    public void setSnmpTrapSer2(final String snmpTrapSer2) {
        final String oldValue = this.snmpTrapSer2;
        this.snmpTrapSer2 = snmpTrapSer2;
        notifyChange(11, oldValue, snmpTrapSer2);
    }

    public int getQinqEtherType() {
        return this.qinqEtherType;
    }

    public void setQinqEtherType(final int qinqEtherType) {
        final int oldValue = this.qinqEtherType;
        this.qinqEtherType = qinqEtherType;
        notifyChange(12, oldValue, qinqEtherType);
    }

    public void set(final VariableBinding binding) {
        switch (binding.getOid().get(10)) {
            case  1 :
                setMacaddress(binding.getVariable().toString());
                break;
            case  2 :
                setConfigfile(binding.getVariable().toInt());
                break;
            case  3 :
                setLfpcontrol(binding.getVariable().toInt());
                break;
            case  4 :
                setSystemtime(binding.getVariable().toInt());
                break;
            case  5 :
                setJamborframe(binding.getVariable().toInt());
                break;
            case  6 :
                setReboot(binding.getVariable().toInt());
                break;
            case  7 :
                setSoftwareVersion(binding.getVariable().toString());
                break;
            case  8 :
                setHardwareVersion(binding.getVariable().toString());
                break;
            case  9 :
                setQinqForwardVID(binding.getVariable().toInt());
                break;
            case  10 :
                setSnmpTrapSer1(binding.getVariable().toString());
                break;
            case  11 :
                setSnmpTrapSer2(binding.getVariable().toString());
                break;
            case  12 :
                setQinqEtherType(binding.getVariable().toInt());
                break;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("_id", _id).append("macaddress", macaddress).append("configfile", configfile).append("lfpcontrol", lfpcontrol).append("systemtime", systemtime).append("jamborframe", jamborframe).append("reboot", reboot).append("softwareVersion", softwareVersion).append("hardwareVersion", hardwareVersion).append("qinqForwardVID", qinqForwardVID).append("snmpTrapSer1", snmpTrapSer1).append("snmpTrapSer2", snmpTrapSer2).append("qinqEtherType", qinqEtherType).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_id).append(macaddress).append(configfile).append(lfpcontrol).append(systemtime).append(jamborframe).append(reboot).append(softwareVersion).append(hardwareVersion).append(qinqForwardVID).append(snmpTrapSer1).append(snmpTrapSer2).append(qinqEtherType).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass()!= this.getClass()) {
            return false;
        }
        final EntityTest1 rhs = ((EntityTest1) obj);
        return new EqualsBuilder().append(_id, rhs._id).append(macaddress, rhs.macaddress).append(configfile, rhs.configfile).append(lfpcontrol, rhs.lfpcontrol).append(systemtime, rhs.systemtime).append(jamborframe, rhs.jamborframe).append(reboot, rhs.reboot).append(softwareVersion, rhs.softwareVersion).append(hardwareVersion, rhs.hardwareVersion).append(qinqForwardVID, rhs.qinqForwardVID).append(snmpTrapSer1, rhs.snmpTrapSer1).append(snmpTrapSer2, rhs.snmpTrapSer2).append(qinqEtherType, rhs.qinqEtherType).isEquals();
    }

    @Override
    public EntityTest1 clone() {
        final EntityTest1 _copy = new EntityTest1();
        _copy._id = _id;
        _copy.parentEntity = parentEntity;
        _copy.macaddress = macaddress;
        _copy.configfile = configfile;
        _copy.lfpcontrol = lfpcontrol;
        _copy.systemtime = systemtime;
        _copy.jamborframe = jamborframe;
        _copy.reboot = reboot;
        _copy.softwareVersion = softwareVersion;
        _copy.hardwareVersion = hardwareVersion;
        _copy.qinqForwardVID = qinqForwardVID;
        _copy.snmpTrapSer1 = snmpTrapSer1;
        _copy.snmpTrapSer2 = snmpTrapSer2;
        _copy.qinqEtherType = qinqEtherType;
        return _copy;
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

}
