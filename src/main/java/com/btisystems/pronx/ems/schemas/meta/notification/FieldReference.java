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

/**
 * The type Field reference.
 */
public class FieldReference implements Serializable {
    private static final long serialVersionUID = 8682116225863546853L;
    /**
     * The Description.
     */
    protected Object description;

    @Override
    public String toString() {
        return "FieldReference{" +
                "description=" + description +
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

        FieldReference that = (FieldReference) o;

        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public Object getDescription() {

        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(Object description) {
        this.description = description;
    }

    /**
     * With description field reference.
     *
     * @param value the value
     * @return the field reference
     */
    public FieldReference withDescription(Object value) {
        setDescription(value);
        return this;
    }

}
