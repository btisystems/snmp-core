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

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.schemas.meta.notification.FieldDescription;

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

    public OID getOid() {
        return oid;
    }

    public FieldDescription getFieldByName(final String name) {
        return fieldsByName.get(name);
    }

    public FieldDescription getFieldByOid(final String oid) {
        final FieldDescription fieldDescription = fieldsByOid.get(oid);
        return fieldDescription;
    }

    public Collection<FieldDescription> getFields() {
        return fieldsByName.values();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
