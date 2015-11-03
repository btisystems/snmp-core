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

import java.util.List;

import org.snmp4j.smi.OID;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface to an auto-generated OID registry.
 */
public interface IClassRegistry extends Serializable {

    /**
     * Delivers the class associated with an OID.
     *
     * @param oid   the OID for which the corresponding class is to be returned
     * @return      the {@link Class} used for the entity corresponding to the managed
     *              object identified by <code>oid</code>, or <code>null</code> if there
     *              is no entry.
     */
    Class<? extends DeviceEntity> getClass(OID oid);

    /**
     * Delivers the class associated with a name.
     *
     * @param name  the simple name of the class to be returned
     * @return      the {@link Class} used for the entity corresponding to the managed
     *              object identified by <code>name</code>, or <code>null</code> if there
     *              is no entry.
     */
    Class<? extends DeviceEntity> getClass(String name);

    /**
     * @return  the {@link AbstractRootEntity} used to hold all entities for the device
     */
    Class<? extends AbstractRootEntity> getRootEntityClass();

    /**
     * @return  list of OIDs for all device entities, in OID order.
     */
    List<OID> getOids();

    /**
     * @return  list of OIDs for tables and scalars (no entries), in OID order.
     */
    List<OID> getDiscoveryOids();

    /**
     * Expects a comma separated list of OIDs to be excluded during a full walk.
     */
    void setExcludedDiscoveryOids(String excludedDiscoveryOids);

    /**
     * Delivers the {@link DeviceEntityDescription} for an entity associated with a given OID.
     *
     * @param oid   the OID for which the corresponding description is to be returned
     * @return      the {@link DeviceEntityDescription} for the entity corresponding to the managed
     *              object identified by <code>oid</code>, or <code>null</code> if there
     *              is no entry.
     */
    DeviceEntityDescription getEntityDescription(OID oid);

    /**
     * Deliver the class of the database entity that contains an element with
     * a specified oid value
     * @param oidValue the value of the OID to be checked
     * @return  the class which contains a field with the specified oid.
     *          If oidValue refers to a table entry, the class of the parent table will be returned.
     *          If oidValue refers to a scalar, the class of the parent entity will be returned.
     *          Otherwise, the entity associated with the specified oid is returned.
     */
    Class<? extends DeviceEntity> getContainingEntityClass(String oidValue);

    /**
     * Lookup to deliver the OID for a given device entity.
     * @return Map containing all entities for this device type.
     */
    Map<Class<? extends DeviceEntity>, OID> getClassToOidMap();
    
}
