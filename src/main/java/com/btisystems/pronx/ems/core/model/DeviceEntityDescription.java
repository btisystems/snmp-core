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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Metadata for device entities.
 */
public class DeviceEntityDescription implements Serializable {

    // The OID associated with the managed object.
    private final OID oid;

    // The variables in the managed object.
    private final Map<String, FieldDescription> fieldsByName = new TreeMap<String, FieldDescription>();
    private final Map<Integer, FieldDescription> fieldsById = new TreeMap<Integer, FieldDescription>();

    /**
     * Instantiates a new Device entity description.
     *
     * @param oid the oid
     */
    public DeviceEntityDescription(final OID oid) {
        this.oid = oid;
    }

    ;

    /**
     * Add field.
     *
     * @param description the description
     */
    public void addField(final FieldDescription description) {
        fieldsByName.put(description.getName(), description);
        fieldsById.put(description.getId(), description);
    }

    /**
     * Gets oid.
     *
     * @return the oid
     */
    public OID getOid() {
        return oid;
    }

    /**
     * Gets field by name.
     *
     * @param name the name
     * @return the field by name
     */
    public FieldDescription getFieldByName(final String name) {
        return fieldsByName.get(name);
    }

    /**
     * Gets field by id.
     *
     * @param id the id
     * @return the field by id
     */
    public FieldDescription getFieldById(final int id) {
        return fieldsById.get(id);
    }

    /**
     * Gets fields.
     *
     * @return the fields
     */
    public Collection<FieldDescription> getFields() {
        return fieldsByName.values();
    }

    /**
     * The enum Field type.
     */
    public enum FieldType {
        /**
         * Integer field type.
         */
        INTEGER, /**
         * String field type.
         */
        STRING, /**
         * Unsigned 32 field type.
         */
        UNSIGNED32, /**
         * Unsigned 64 field type.
         */
        UNSIGNED64, /**
         * Ip address field type.
         */
        IP_ADDRESS, /**
         * Date and time field type.
         */
        DATE_AND_TIME, /**
         * Bits field type.
         */
        BITS, /**
         * Table field type.
         */
        TABLE, /**
         * Entity field type.
         */
        ENTITY, /**
         * Fixed x 10 field type.
         */
        FIXED_X10, /**
         * Fixed x 100 field type.
         */
        FIXED_X100, /**
         * Fixed x 1000 field type.
         */
        FIXED_X1000, /**
         * Oid field type.
         */
        OID
    }

    /**
     * The type Field description.
     */
    public static class FieldDescription implements Serializable {
        // The OID subidentifier value for the field.
        // If the managed object's OID is 1.2.3, the field with the OID 1.2.3.4 will have id 4.
        private final int id;
        private final String name;
        private final FieldType type;
        private final int maximumLength; // Only for string types, otherwise -1.

        /**
         * Instantiates a new Field description.
         *
         * @param id            the id
         * @param name          the name
         * @param type          the type
         * @param maximumLength the maximum length
         */
        public FieldDescription(final int id, final String name, final FieldType type, final int maximumLength) {
            super();
            this.id = id;
            this.name = name;
            this.type = type;
            this.maximumLength = maximumLength;
        }

        /**
         * Gets id.
         *
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets type.
         *
         * @return the type
         */
        public FieldType getType() {
            return type;
        }

        /**
         * Gets maximum length.
         *
         * @return the maximum length
         */
        public int getMaximumLength() {
            return maximumLength;
        }

        @Override
        public String toString() {
            return "FieldDescription{" + "id=" + id + ", name=" + name + ", type=" + type + ", maximumLength=" + maximumLength + '}';
        }

    }
}
