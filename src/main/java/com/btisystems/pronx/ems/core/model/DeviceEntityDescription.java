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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.snmp4j.smi.OID;

/**
 * Metadata for device entities.
 */

public class DeviceEntityDescription implements Serializable {

    // The OID associated with the managed object.
    private final OID oid;

    // The variables in the managed object.
    private final Map<String, FieldDescription> fieldsByName = new TreeMap<String, FieldDescription>();
    private final Map<Integer, FieldDescription> fieldsById = new TreeMap<Integer, FieldDescription>();

    public enum FieldType { INTEGER, STRING, UNSIGNED32, UNSIGNED64, IP_ADDRESS, DATE_AND_TIME, BITS, TABLE, ENTITY, FIXED_X10, FIXED_X100, FIXED_X1000, OID };

    public DeviceEntityDescription(final OID oid) {
        this.oid = oid;
    }

    public void addField(final FieldDescription description) {
        fieldsByName.put(description.getName(), description);
        fieldsById.put(description.getId(), description);
    }

    public OID getOid() {
        return oid;
    }

    public FieldDescription getFieldByName(final String name) {
        return fieldsByName.get(name);
    }

    public FieldDescription getFieldById(final int id) {
        return fieldsById.get(id);
    }

    public Collection<FieldDescription> getFields() {
        return fieldsByName.values();
    }

    public static class FieldDescription implements Serializable {
        // The OID subidentifier value for the field.
        // If the managed object's OID is 1.2.3, the field with the OID 1.2.3.4 will have id 4.
        private final int id;
        private final String name;
        private final FieldType type;
        private final int maximumLength; // Only for string types, otherwise -1.

        public FieldDescription(final int id, final String name, final FieldType type, final int maximumLength) {
            super();
            this.id = id;
            this.name = name;
            this.type = type;
            this.maximumLength = maximumLength;
        }

        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public FieldType getType() {
            return type;
        }
        public int getMaximumLength() {
            return maximumLength;
        }

        @Override
        public String toString() {
            return "FieldDescription{" + "id=" + id + ", name=" + name + ", type=" + type + ", maximumLength=" + maximumLength + '}';
        }

    }
}
