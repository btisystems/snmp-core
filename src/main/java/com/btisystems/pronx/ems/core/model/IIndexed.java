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

import org.snmp4j.smi.OID;

/**
 * The interface supported by an entity for an indexed managed object.
 * <p/>
 * An indexed managed object is one that is used to populate a table, and
 * the index is used to identify a specific instance of the entity in the
 * table.
 */
public interface IIndexed {

    /**
     * Set the index of this to values defined by an OID.
     *
     * @param oid an OID that ends with the index to be set for this object.
     */
    void _setIndex(OID oid);

    /**
     * Get index string.
     *
     * @return the index of this entity instance, as a string.
     */
    String _getIndex();
}
