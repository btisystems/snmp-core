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
package com.btisystems.pronx.ems.core.snmp.trapsender;

import java.io.Serializable;
import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.core.model.DeviceEntity;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import com.btisystems.pronx.ems.core.model.INotification;
import java.util.Date;
import java.util.Objects;

/**
 * The type Notification.
 */
public class Notification extends DeviceEntity implements Serializable, INotification {

	private static final long serialVersionUID = 1L;

	private static final DeviceEntityDescription DESCRIPTION = createEntityDescription();

    private int id;
    private String ip;
    private String string;
    private int unsigned;
    private String dateTime;

    /**
     * Instantiates a new Notification.
     */
    public Notification() {
    }

    /**
     * Instantiates a new Notification.
     *
     * @param id       the id
     * @param ip       the ip
     * @param string   the string
     * @param unsigned the unsigned
     */
    public Notification(final int id, final String ip, final String string, final int unsigned, final String dateTime) {
        this.id = id;
        this.ip = ip;
        this.string = string;
        this.unsigned = unsigned;
        this.dateTime = dateTime;
    }


    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(final int id) {
        final int oldValue = this.id;
        this.id = id;
        notifyChange(1, oldValue, id);
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(final String ip) {
        final String oldValue = this.ip;
        this.ip = ip;
        notifyChange(4, oldValue, ip);
    }

    /**
     * Gets string.
     *
     * @return the string
     */
    public String getString() {
        return this.string;
    }

    /**
     * Sets string.
     *
     * @param string the string
     */
    public void setString(final String string) {
        final String oldValue = this.string;
        this.string = string;
        notifyChange(5, oldValue, string);
    }

    /**
     * Gets unsigned.
     *
     * @return the unsigned
     */
    public int getUnsigned() {
        return unsigned;
    }

    /**
     * Sets unsigned.
     *
     * @param unsigned the unsigned
     */
    public void setUnsigned(final int unsigned) {
        this.unsigned = unsigned;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(final String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.id;
        hash = 71 * hash + Objects.hashCode(this.ip);
        hash = 71 * hash + Objects.hashCode(this.string);
        hash = 71 * hash + this.unsigned;
        hash = 71 * hash + Objects.hashCode(this.dateTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.ip, other.ip)) {
            return false;
        }
        if (!Objects.equals(this.string, other.string)) {
            return false;
        }
        if (this.unsigned != other.unsigned) {
            return false;
        }
        if (!Objects.equals(this.dateTime, other.dateTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Notification{" + "id=" + id + ", ip=" + ip + ", string=" + string + ", unsigned=" + unsigned + ", dateTime=" + dateTime + '}';
    }

    @Override
    public Notification clone() {
        final Notification copy = new Notification();
        copy.id = id;
        copy.ip = ip;
        copy.string = string;
        copy.unsigned = unsigned;
        copy.dateTime = dateTime;
        return copy;
    }

    private static DeviceEntityDescription createEntityDescription() {
        final DeviceEntityDescription newDescription = new DeviceEntityDescription(new OID("1"));
        newDescription.addField(new DeviceEntityDescription.FieldDescription(1, "id", DeviceEntityDescription.FieldType.INTEGER, -1));
        newDescription.addField(new DeviceEntityDescription.FieldDescription(2, "ip", DeviceEntityDescription.FieldType.IP_ADDRESS, -1));
        newDescription.addField(new DeviceEntityDescription.FieldDescription(3, "string", DeviceEntityDescription.FieldType.STRING, -1));
        newDescription.addField(new DeviceEntityDescription.FieldDescription(4, "unsigned", DeviceEntityDescription.FieldType.UNSIGNED32, -1));
        newDescription.addField(new DeviceEntityDescription.FieldDescription(5, "dateTime", DeviceEntityDescription.FieldType.DATE_AND_TIME, -1));
        return newDescription;
    }

    @Override
    public DeviceEntityDescription get_Description() {
        return DESCRIPTION;
    }
}
