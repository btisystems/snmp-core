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
package com.btisystems.pronx.ems.schemas.meta.notification;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The type Notification definition.
 */
public class NotificationDefinition implements Serializable {

    private static final long serialVersionUID = 5123538049407565227L;
    /**
     * The Field reference.
     */
    protected List<FieldReference> fieldReference;

    /**
     * The Oid.
     */
    protected String oid;

    /**
     * The Name.
     */
    protected String name;

    /**
     * The Description.
     */
    protected String description;

    @Override
    public int hashCode() {
        int result = fieldReference != null ? fieldReference.hashCode() : 0;
        result = 31 * result + (oid != null ? oid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NotificationDefinition that = (NotificationDefinition) o;

        if (fieldReference != null ? !fieldReference.equals(that.fieldReference) : that.fieldReference != null) {
            return false;
        }
        if (oid != null ? !oid.equals(that.oid) : that.oid != null) {
            return false;
        }
        return !(name != null ? !name.equals(that.name) : that.name != null) && !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public String toString() {
        return "NotificationDefinition{" +
                "fieldReference=" + fieldReference +
                ", oid='" + oid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Gets oid.
     *
     * @return the oid
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets oid.
     *
     * @param oid the oid
     */
    public void setOid(String oid) {
        this.oid = oid;
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
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * With field reference notification definition.
     *
     * @param values the values
     * @return the notification definition
     */
    public NotificationDefinition withFieldReference(FieldReference... values) {
        if (values != null) {
            for (FieldReference value : values) {
                getFieldReference().add(value);
            }
        }
        return this;
    }

    /**
     * Gets field reference.
     *
     * @return the field reference
     */
    public List<FieldReference> getFieldReference() {
        if (fieldReference == null) {
            fieldReference = new ArrayList<FieldReference>();
        }
        return this.fieldReference;
    }

    /**
     * Sets field reference.
     *
     * @param fieldReference the field reference
     */
    public void setFieldReference(List<FieldReference> fieldReference) {
        this.fieldReference = fieldReference;
    }

    /**
     * With field reference notification definition.
     *
     * @param values the values
     * @return the notification definition
     */
    public NotificationDefinition withFieldReference(Collection<FieldReference> values) {
        if (values != null) {
            getFieldReference().addAll(values);
        }
        return this;
    }

    /**
     * With oid notification definition.
     *
     * @param value the value
     * @return the notification definition
     */
    public NotificationDefinition withOid(String value) {
        setOid(value);
        return this;
    }

    /**
     * With name notification definition.
     *
     * @param value the value
     * @return the notification definition
     */
    public NotificationDefinition withName(String value) {
        setName(value);
        return this;
    }

    /**
     * With description notification definition.
     *
     * @param value the value
     * @return the notification definition
     */
    public NotificationDefinition withDescription(String value) {
        setDescription(value);
        return this;
    }
}
