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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;


/**
 * Manages the creation and population of entities for a discovered device.
 */

public class NetworkDevice implements INetworkDevice {

    private static Logger log = LoggerFactory.getLogger(NetworkDevice.class);

    private final IClassRegistry oidRegistry;

    private final Map<OID, DeviceEntity> simpleObjects = new HashMap<OID, DeviceEntity>();

    private AbstractRootEntity rootEntity;

    private final String deviceAddress;

    /**
     * Class constructor
     *
     * @param oidRegistry   the registry used to map OIDs to entity classes
     * @param deviceAddress the name of the device
     */
    public NetworkDevice(final IClassRegistry oidRegistry,
                  final String deviceAddress) {
        super();
        this.oidRegistry = oidRegistry;
        this.deviceAddress = deviceAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVariable(final VariableBinding binding) {
        log.debug(">>> addVariable:{}", binding);

        final OID containingOid = getContainingClassOid(binding.getOid());
        if (containingOid != null) {

            // Determine the number of index elements in the OID for the variable.
            // This is difference in the length of its OID and the containing object's OID.
            final int indexElementCount = binding.getOid().size() - containingOid.size();
            log.trace("indexElementCount:{}", indexElementCount);

            // We have got a class that can potentially contain the variable.
            // See if that class is a table entry (i.e. it has an index).
            final Class<? extends DeviceEntity> containingClass = oidRegistry.getClass(containingOid);

            // If there is no entry index, we must be dealing with a scalar variable.
            if (!IIndexed.class.isAssignableFrom(containingClass)) {

                // Check that the OID of the containing object is the same as the
                // variable, without the variable identifier and the final ".0".
                if (indexElementCount == 2) {
                    // Set the variable on the containing object
                    addScalarVariable(binding, containingOid);
                } else {
                    log.debug("Unrecognised OID:" + binding.getOid());
                    return false;
                }
            } else {
                // This must be a variable in a table element.
                addTableColumn(binding, containingOid, indexElementCount - 1);
            }
            return true;
        } else {
            log.info("Ignoring oid {} from {}.", binding.getOid(), deviceAddress);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRootEntity getRootObject() {
        return getRootEntity();
    }

    // Add scalar variable to its object
    private void addScalarVariable(final VariableBinding binding,
                                   final OID containingOid) {
        log.debug(">>> addScalarVariable:{} {}", binding, containingOid);

        // Locate/create the instance of the class for the parent object.
        final Object containingObject = getObject(containingOid);

        if (containingObject != null) {
            // Set the variable on the parent object.
            setVariable(containingObject, binding);
        } else {
            log.debug("Failed to find class for:{}", containingOid);
        }
    }

    // Add table column to table entry
    private void addTableColumn(final VariableBinding binding,
                                final OID entryOid,
                                final int indexElementCount) {
        log.debug(">>> addTableColumn:{} {}", binding, entryOid);

        final String entryIdentifier = getTableEntryIdentifier(binding.getOid(), indexElementCount);
        log.debug("entryIdentifier:{}", entryIdentifier);

        // Determine whether this table entry already exists.
        final Object tableEntry = getTableEntry(entryOid, binding.getOid(), entryIdentifier);

        if (tableEntry != null) {
            // Set the variable on the table entry
            setVariable(tableEntry, binding);
        }
        log.debug("<<< addTableColumn");
    }

    // Deliver the table entry for the specified OID and entryIdentifier
    @SuppressWarnings("unchecked")
    private Object getTableEntry(final OID entryOid,
                                 final OID variableOid,
                                 final String entryIdentifier) {

        // Check we recognise the table entry.
        final Class<? extends DeviceEntity> clazz = oidRegistry.getClass(entryOid);
        if (clazz == null) {
            log.debug("Unrecognized table entry:{}", entryOid);
            return null;
        }

        // Get the table to which the entry is to be added
        final DeviceEntity entity = getObject(getParentOid(entryOid));
        assert entity != null : "Failed to get table object";
        final ITableAccess<DeviceEntity> table = (ITableAccess<DeviceEntity>) entity;

        // See if the specific entry already exists for the table.
        final Object entryObject = table.getEntry(entryIdentifier);
        if (entryObject != null) {
            return entryObject;
        }

        // Need to create a new table entry.
        final Object newEntryObject = instantiateObject(clazz);
        if (newEntryObject != null) {
            table.setEntry(entryIdentifier, (DeviceEntity) newEntryObject);
            ((IIndexed) newEntryObject)._setIndex(variableOid);
            return newEntryObject;
        }
        return null;
    }

    // Sets variable on a managed object.
    private void setVariable(final Object parentObject,
                             final VariableBinding binding) {
        log.debug(">>> setVariable oid:{} object:{}", binding.getOid(), parentObject.getClass().getName());
        ((IVariableBindingSetter) parentObject).set(binding);
    }

    // Gets the instance of the managed object identified by its OID.
    private DeviceEntity getObject(final OID oid) {
        // See if the object has already been created.
        final DeviceEntity object = simpleObjects.get(oid);
        if (object != null) {
            return object;
        }

        // Need to instantiate a new entity.
        final DeviceEntity newObject = createNewEntity(oid);
        return newObject;
    }

    // Given the OID of a class, instantiates a new object and adds it to the root entity.
    private DeviceEntity createNewEntity(final OID oid) {
        final Class<? extends DeviceEntity> clazz = oidRegistry.getClass(oid);
        if (clazz == null) {
            log.debug("No class for oid:{}", oid);
            return null;
        }

        // Instantiate the new object.
        final DeviceEntity object = instantiateObject(clazz);
        if (object != null) {
            // Add to the simple objects.
            simpleObjects.put(oid, object);

            // Add to the root entity for the device
            final AbstractRootEntity rootEntity = getRootEntity();
            rootEntity.setObject(object);
            return object;
        }
        return null;
    }

    // Deliver the root entity for the device, creating it if necessary.
    private AbstractRootEntity getRootEntity() {
        log.debug(">>> getRootEntity");

        if (rootEntity == null) {
            final Class<? extends AbstractRootEntity> rootEntityClass = oidRegistry.getRootEntityClass();
            try {
                log.debug("instantiating entity type:{}", rootEntityClass);
                rootEntity = rootEntityClass.newInstance();
                log.debug("set device address:{}", deviceAddress);
                rootEntity.setDeviceAddress(deviceAddress);
            } catch (final Exception e) {
                log.debug("Failed to create root entity:", e);
                log.error("Failed to create root entity:{} {}", rootEntityClass, e.getMessage());
            }
        }
        return rootEntity;
    }

    // Instantiates an object of a given class.
    private DeviceEntity instantiateObject(final Class<? extends DeviceEntity> clazz) {
        DeviceEntity object = null;
        try {
            object = clazz.newInstance();
        } catch (final InstantiationException e) {
            log.warn("Failed to instantiate object of type a {} {}", clazz, e.getMessage());
            log.debug("Failed to instantiate object of type a {}:", clazz, e);
        } catch (final IllegalAccessException e) {
            log.warn("Failed to instantiate object of type b {} {}", clazz, e.getMessage());
            log.debug("Failed to instantiate object of type b {}:", clazz, e);
        }
        return object;
    }


    // Return the table entry index from an OID.
    private String getTableEntryIdentifier(final OID oid, final int length) {
        final int[] rawOid = oid.getValue();
        final StringBuilder sb = new StringBuilder();
        final int startIndex = rawOid.length - length;
        for (int i = startIndex; i < rawOid.length; i++) {
            if (i > startIndex) {
                sb.append('.');
            }
            sb.append(rawOid[i]);
        }
        return sb.toString();
    }

    // Returns the parent OID of an OID.
    private OID getParentOid(final OID oid) {
        return getAncestorOid(oid, 1);
    }

    private OID getAncestorOid(final OID oid, final int generationCount) {
        final int[] rawOid = oid.getValue();
        final int levels = oid.size();
        if (levels <= generationCount) {
            log.debug("Unexpected OID Ancestor {} {}", oid, generationCount);
            return null;
        }

        return new OID(rawOid, 0, levels - generationCount);
    }

    // Determine the OID of the class that contains the variable with the OID
    private OID getContainingClassOid(final OID oid) {
        // Keep trying parent Ids until we get a match in the registry
        OID parentOid = getParentOid(oid);
        while (parentOid != null && oidRegistry.getClass(parentOid) == null) {
            parentOid = getParentOid(parentOid);
        }
        log.trace("<<< getContainingClassOid {}", parentOid);
        return parentOid;
    }
}
