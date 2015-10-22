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


/**
 * The enum Field type.
 */
public enum FieldType {

    /**
     * Integer field type.
     */
    INTEGER,
    /**
     * String field type.
     */
    STRING;

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /**
     * From value field type.
     *
     * @param v the v
     * @return the field type
     */
    public static FieldType fromValue(String v) {
        return valueOf(v);
    }

}