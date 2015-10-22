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

import java.beans.PropertyChangeListener;

public interface IDeviceEntity extends Cloneable {

    /**
     * @return  the description for the device entity
     */
    DeviceEntityDescription get_Description();

    String getString(final String fieldName);

    int getInt(final String fieldName);

    long getLong(final String fieldName);

    void set(final String fieldName, final String value);

    void set(final String fieldName, final int value);

    void set(final String fieldName, final long value);

    void addPropertyChangeListener(final PropertyChangeListener listener);

    void removePropertyChangeListener(final PropertyChangeListener listener);

    void clearPropertyChangeListeners();

    void set_ParentEntity(final AbstractRootEntity parent);

    /**
     * @return  true/false according to whether named field is supported by the entity.
     */
    boolean isSupported(String fieldName);
}
