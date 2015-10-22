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
import java.util.Collection;
import java.util.List;

/**
 * The type Field list.
 */
public class FieldList implements Serializable {
    private static final long serialVersionUID = -263065358323362154L;
    /**
     * The Field description.
     */
    protected List<FieldDescription> fieldDescription;

    @Override
    public String toString() {
        return "FieldList{" +
                "fieldDescription=" + fieldDescription +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldList fieldList = (FieldList) o;

        return !(fieldDescription != null ? !fieldDescription.equals(fieldList.fieldDescription) : fieldList.fieldDescription != null);

    }

    @Override
    public int hashCode() {
        return fieldDescription != null ? fieldDescription.hashCode() : 0;
    }

    /**
     * Gets field description.
     *
     * @return the field description
     */
    public List<FieldDescription> getFieldDescription() {

        return fieldDescription;
    }

    /**
     * Sets field description.
     *
     * @param fieldDescription the field description
     */
    public void setFieldDescription(List<FieldDescription> fieldDescription) {
        this.fieldDescription = fieldDescription;
    }


    /**
     * With field description field list.
     *
     * @param values the values
     * @return the field list
     */
    public FieldList withFieldDescription(FieldDescription... values) {
        if (values!= null) {
            for (FieldDescription value: values) {
                getFieldDescription().add(value);
            }
        }
        return this;
    }

    /**
     * With field description field list.
     *
     * @param values the values
     * @return the field list
     */
    public FieldList withFieldDescription(Collection<FieldDescription> values) {
        if (values!= null) {
            getFieldDescription().addAll(values);
        }
        return this;
    }


}
