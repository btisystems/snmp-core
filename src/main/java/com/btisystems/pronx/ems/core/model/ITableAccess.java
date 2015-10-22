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

import java.util.Map;

/**
 * Interface supported by auto-generated objects that implement methods to access a table
 */
public interface ITableAccess<T extends IDeviceEntity> {

    /**
     * Deliver table entry with the specified index
     *
     * @param index     the index of the entry whose value is to be returned
     *
     * @return          the value to which the specified index is mapped, or null if this map
     *                  contains no mapping for the key
     */
    T getEntry(String index);

    /**
     * Associates the specified entry with the specified index in the table.
     * If the map previously contained a mapping for the index, the old entry is replaced.
     *
     * @param index     the index with which the specified entry is to be associated
     * @param entry     entry to be associated with the specified index
     */
    void setEntry(String index, T entry);

    /**
     * @return  the map used to contain the table entries
     */
    Map<String, T> getEntries();

    /**
     * @return a new instance of a table entry.
     */
    T createEntry(String index);
}
