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


/**
 * Interface supported by auto-generated objects that implement method to set a
 * field from an object.
 */
public interface IObjectSetter {

    /**
     * Set a field to a specified object.
     *
     * @param clazz       the class of object to be set.
     * @param childObject the value to which the field defined by childOid is to be set
     */
    void setObject(Class<DeviceEntity> clazz, Object childObject);

    /**
     * Sets a specified object on an entity
     *
     * @param childObject the object to be set. This must not be null
     */
    void setObject(Object childObject);
}
