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
import com.btisystems.pronx.ems.schemas.meta.notification.FieldType;
import org.junit.Test;
import org.snmp4j.smi.OID;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NotificationDescriptionTest {

    @Test
    public void shouldGetNotificationDescriptionDetails()  {
        final NotificationDescription description = createNotificationDescription();
        assertEquals("N Name", description.getName());
        assertEquals("N Desc", description.getDescription());
        assertEquals("1.3.6.1.4", description.getOid().toString());
        assertEquals(3, description.getFields().size());

    }

    @Test
    public void shouldGetFieldDescriptionByName()  {
        final NotificationDescription description = createNotificationDescription();
        final FieldDescription fieldDescription = description.getFieldByName("stmnTypeIdx");
        assertEquals("stmnTypeIdx", fieldDescription.getName());
        assertEquals("The specific type", fieldDescription.getDescription());
        assertEquals(FieldType.INTEGER, fieldDescription.getType());
        assertEquals("1.3.6.1.4.1", fieldDescription.getOid());
    }

    @Test
    public void shouldGetFieldDescriptionByOid()  {
        final NotificationDescription description = createNotificationDescription();
        final FieldDescription fieldDescription = description.getFieldByOid("1.3.6.1.4.3");
        assertEquals("stmnOperStatQlfr", fieldDescription.getName());
        assertEquals("This is a textual ", fieldDescription.getDescription());
        assertEquals(FieldType.STRING, fieldDescription.getType());
        assertEquals("1.3.6.1.4.3", fieldDescription.getOid());
    }

    private NotificationDescription createNotificationDescription() {
        return new NotificationDescription(new OID("1.3.6.1.4"),
                "N Name",
                "N Desc",
                createFieldDescriptionList());
    }

    private List<FieldDescription> createFieldDescriptionList() {
         final List<FieldDescription> fieldDescriptions = new ArrayList<FieldDescription>();
         fieldDescriptions.add(createFieldDescription("1.3.6.1.4.1", "stmnTypeIdx", "The specific type", FieldType.INTEGER));
         fieldDescriptions.add(createFieldDescription("1.3.6.1.4.2", "stmnShelfIdx", "The number of", FieldType.INTEGER));
         fieldDescriptions.add(createFieldDescription("1.3.6.1.4.3", "stmnOperStatQlfr", "This is a textual ", FieldType.STRING));
         return fieldDescriptions;
    }

    private FieldDescription createFieldDescription(final String oid, final String name, final String description, final FieldType type) {
        return new FieldDescription().withOid(oid).withName(name).withDescription(description).withType(type);
    }
}
