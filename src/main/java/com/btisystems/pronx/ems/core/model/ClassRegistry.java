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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import com.btisystems.pronx.ems.core.snmp.trapsender.VarBindComparator;

/**
 * Manages the creation and population of entities for a discovered device.
 */
public class ClassRegistry implements IClassRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ClassRegistry.class);
    private static final long serialVersionUID = 1L;
    private final TreeMap<OID, Class<? extends DeviceEntity>> oidToClassMap;
    private final HashMap<String, Class<? extends DeviceEntity>> nameToClassMap;
    private final Class<? extends AbstractRootEntity> rootEntityClass;

    private List<OID> excludedDiscoveryOids = new ArrayList<>();
    private Map<Class<? extends DeviceEntity>, OID> classToOidMap = new HashMap<>();

    /**
     * Class constructor
     *
     * @param oidRegistry   the registry used to map OIDs to entity classes
     * @param rootEntityClass     the root entity class
     */
    public ClassRegistry(final TreeMap<OID, Class<? extends DeviceEntity>> oidRegistry,
            final Class<? extends AbstractRootEntity> rootEntityClass) {
        LOG.debug(">>> ClassRegistry");
        this.oidToClassMap = oidRegistry;
        this.rootEntityClass = rootEntityClass;
        this.classToOidMap = createClassToOidMap(oidRegistry);
        nameToClassMap = createNameToClassMap(oidRegistry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends AbstractRootEntity> getRootEntityClass() {
        return rootEntityClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends DeviceEntity> getClass(final OID oid) {
        return oidToClassMap.get(oid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends DeviceEntity> getClass(final String name) {
        return nameToClassMap.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OID> getOids() {
        return new ArrayList<>(oidToClassMap.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OID> getDiscoveryOids() {
        List<OID> oids = new ArrayList<>();
        for (final Map.Entry<OID, Class<? extends DeviceEntity>> entry : oidToClassMap.entrySet()) {
            if (!IIndexed.class.isAssignableFrom(entry.getValue())) {
                oids.add(entry.getKey());
            }
        }
        oids = processExcludedOIDs(oids);
        LOG.trace("Returning discovery oids: {}", oids);
        return oids;
    }

    @Override
    public Map<Class<? extends DeviceEntity>, OID> getClassToOidMap() {
        return classToOidMap;
    }

    /**
     * Processes the oid list against the excluded oids set on this registry.
     * For each excluded oid:
     *  - The oid is removed from the list.
     *  - The parent oid (ie the branch) is also removed to prevent sub-tree walk of excluded oid.
     *  - The sub-oids of the parent are added back in.
     *
     * This ensures the excluded oid won't be walked on its own, or as part of a sub-tree walk, but also that
     * sub-oids which aren't explicitely excluded are still walked.
     *
     * @param oids
     * @return the oid list.
     */
    private List<OID> processExcludedOIDs(final List<OID> oids) {
        LOG.debug("Excluding oids from discovery list: {}", excludedDiscoveryOids);
        for (final OID excludedOid : excludedDiscoveryOids) {
            final List<OID> oidsToRemove = new ArrayList<>();
            final List<OID> oidsToAdd = new ArrayList<>();
            for (final OID discoveryOid : oids) {
                if (discoveryOid.toString().startsWith(excludedOid.toString())){
                    oidsToRemove.add(discoveryOid);
                }
                final OID excludedBranch = new OID(excludedOid).trim();
                if (discoveryOid.equals(excludedBranch)){
                    oidsToRemove.add(discoveryOid);
                    reAddScalars(excludedBranch, oidsToAdd);
                }
            }

            for (final OID oid : oidsToAdd) {
                if (!oids.contains(oid)){
                    oids.add(oid);
                }
            }
            oids.removeAll(oidsToRemove);

        }
        sortOids(oids);
        return oids;
    }

    private void sortOids(final List<OID> oids) {
        Collections.sort(oids, new Comparator<OID>(){
            private final VarBindComparator alphaNumComparator = new VarBindComparator();
            @Override
            public int compare(final OID o1, final OID o2) {
                return alphaNumComparator.compare(o1.toString(), o2.toString());
            }
        });
    }

    private void reAddScalars(final OID excludedBranch, final List<OID> oidsToAdd) {
        final Class<? extends DeviceEntity> excludedClazz = oidToClassMap.get(excludedBranch);
        try {
            if (excludedClazz != null){
                final DeviceEntity excludedEntity = excludedClazz.newInstance();
                for (final FieldDescription fieldDescription : excludedEntity.get_Description().getFields()) {
                    oidsToAdd.add(new OID(excludedEntity.get_Description().getOid()).append(fieldDescription.getId()));
                }
            }
        } catch (final Exception ex) {
            LOG.error("Error adding scalar attributes back in for excluded branch: {}", excludedBranch, ex);
        }
    }

    @Override
    public void setExcludedDiscoveryOids(final String oids) {
        if (oids != null) {
            // Expect a comma-separated list of oids.
            final List<OID> oidList = new ArrayList<>();
            for (final String oid : oids.split(",")) {
                oidList.add(new OID(oid));
            }
            excludedDiscoveryOids = oidList;
        }
    }

    private HashMap<String, Class<? extends DeviceEntity>> createNameToClassMap(final TreeMap<OID, Class<? extends DeviceEntity>> oidRegistry) {
        final HashMap<String, Class<? extends DeviceEntity>> newNameToClassMap = new HashMap<>();
        for (final Class<? extends DeviceEntity> entityClass : oidRegistry.values()) {
            newNameToClassMap.put(entityClass.getSimpleName(), entityClass);
        }
        return newNameToClassMap;
    }
    
    private HashMap<Class<? extends DeviceEntity>, OID> createClassToOidMap(final TreeMap<OID, Class<? extends DeviceEntity>> oidRegistry) {
        final HashMap<Class<? extends DeviceEntity>, OID> classToOidMap = new HashMap<>();
        for (Map.Entry<OID, Class<? extends DeviceEntity>> entrySet : oidRegistry.entrySet()) {
            classToOidMap.put(entrySet.getValue(), entrySet.getKey());
        }
        return classToOidMap;
    }

    @Override
    public DeviceEntityDescription getEntityDescription(final OID oid) {
        final Class<? extends DeviceEntity> entityClazz = oidToClassMap.get(oid);
        // TODO SJ Make autogen generate a static method to avoid instantiation.
        try {
            if (entityClazz != null) {
                final DeviceEntity entity = entityClazz.newInstance();
                return entity.get_Description();
            }
        } catch (final Exception ex) {
            LOG.error("Error retrieving entity description : {}", oid, ex);
        }
        return null;
    }


	@Override
    public Class<? extends DeviceEntity> getContainingEntityClass(final String oidValue) {

        // If the parent of the supplied OID represents a mib table or scalars grouping, return the parent class.
        // Otherwise, just return the child class.

        LOG.debug("oid={}", oidValue);
        final OID baseOid = new OID(oidValue);
        final Class<? extends DeviceEntity> childClass = getClass(baseOid);
        LOG.debug("child class={}", childClass);
        baseOid.trim(1);
        final Class<? extends DeviceEntity> parentClass = getClass(baseOid);
        LOG.debug("parent class={}", parentClass);
        if (parentClass != null && (isClassForAMibTable(parentClass) || isClassForAVarBindsSetter(parentClass))) {
            return parentClass;
        }
        return childClass;
    }

    // Return true only if class represents a Mib Table.
    private boolean isClassForAMibTable(final Class<? extends DeviceEntity> clazz) {
        return ITableAccess.class.isAssignableFrom(clazz);
    }

    private boolean isClassForAVarBindsSetter(final Class<? extends DeviceEntity> clazz) {
        return IVariableBindingSetter.class.isAssignableFrom(clazz);
    }
}
