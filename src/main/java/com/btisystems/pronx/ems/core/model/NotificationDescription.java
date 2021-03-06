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

import com.btisystems.pronx.ems.schemas.meta.notification.FieldDescription;
import org.snmp4j.smi.OID;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * MIB-derived metadata for notifications.
 */
public class NotificationDescription {

    // The OID associated with the notification.
    private final OID oid;

    // The variables in the managed object.
    private final TreeMap<String, FieldDescription> fieldsByName = new TreeMap<>();
    private final TreeMap<String, FieldDescription> fieldsByOid = new TreeMap<>();

    private final String name;
    private final String description;

    /**
     * Instantiates a new Notification description.
     *
     * @param oid         the oid
     * @param name        the name
     * @param description the description
     * @param fields      the fields
     */
    public NotificationDescription(final OID oid,
                                   final String name,
                                   final String description,
                                   final List<FieldDescription> fields) {
        this.oid = oid;
        this.name = name;
        this.description = description;
        if (fields != null) {
            for (final FieldDescription field : fields) {
                addField(field);
            }
        }
    }

    private void addField(final FieldDescription description) {
        fieldsByName.put(description.getName(), description);
        fieldsByOid.put(description.getOid(), description);
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
     * Gets field by oid.
     *
     * @param oid the oid
     * @return the field by oid
     */
    public FieldDescription getFieldByOid(final String oid) {
        final FieldDescription fieldDescription = fieldsByOid.get(oid);
        return fieldDescription;
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
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
