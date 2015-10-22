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

/**
 * The interface Device entity.
 */
public interface IDeviceEntity extends Cloneable {

    /**
     * Gets description.
     *
     * @return the description for the device entity
     */
    DeviceEntityDescription get_Description();

    /**
     * Gets string.
     *
     * @param fieldName the field name
     * @return the string
     */
    String getString(final String fieldName);

    /**
     * Gets int.
     *
     * @param fieldName the field name
     * @return the int
     */
    int getInt(final String fieldName);

    /**
     * Gets long.
     *
     * @param fieldName the field name
     * @return the long
     */
    long getLong(final String fieldName);

    /**
     * Set.
     *
     * @param fieldName the field name
     * @param value     the value
     */
    void set(final String fieldName, final String value);

    /**
     * Set.
     *
     * @param fieldName the field name
     * @param value     the value
     */
    void set(final String fieldName, final int value);

    /**
     * Set.
     *
     * @param fieldName the field name
     * @param value     the value
     */
    void set(final String fieldName, final long value);

    /**
     * Add property change listener.
     *
     * @param listener the listener
     */
    void addPropertyChangeListener(final PropertyChangeListener listener);

    /**
     * Remove property change listener.
     *
     * @param listener the listener
     */
    void removePropertyChangeListener(final PropertyChangeListener listener);

    /**
     * Clear property change listeners.
     */
    void clearPropertyChangeListeners();

    /**
     * Sets parent entity.
     *
     * @param parent the parent
     */
    void set_ParentEntity(final AbstractRootEntity parent);

    /**
     * Is supported boolean.
     *
     * @param fieldName the field name
     * @return true /false according to whether named field is supported by the entity.
     */
    boolean isSupported(String fieldName);
}
