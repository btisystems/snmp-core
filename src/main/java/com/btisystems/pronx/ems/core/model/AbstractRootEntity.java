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

import com.btisystems.pronx.ems.core.exception.FieldAccessMethodException;
import com.btisystems.pronx.ems.core.exception.InvalidFieldNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.lang.reflect.Method;

/**
 * Abstract class to be extended by the root entity for each device type.
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractRootEntity extends DeviceEntity implements IObjectSetter {

    private static Logger log = LoggerFactory.getLogger(AbstractRootEntity.class);

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    private String deviceAddress;

    /**
     * Gets device address.
     *
     * @return the device address
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * Sets device address.
     *
     * @param deviceAddress the device address
     */
    public void setDeviceAddress(final String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    /**
     * Is entity supported boolean.
     *
     * @param fieldName the field name
     * @return the boolean
     */
    public boolean isEntitySupported(final String fieldName) {
        boolean result = true;
        try {
            this.getClass().getMethod(getGetterName(fieldName));
        } catch (final NoSuchMethodException ex) {
            log.trace("No such field on class {}: {}", this.getClass().getName(), fieldName, ex);
            result = false;
        }
        return result;
    }

    /**
     * Returns the entity with the specified OID.
     *
     * @param oid the OID of the entity to be returned
     * @return the {@link DeviceEntity} with the specified oid, which may be <code>null</code>          if the entity has not been instantiated, or if the oid is not registered for          the device.
     */
    public DeviceEntity getEntity(final OID oid) {
        for (final DeviceEntity root : getRoots()) {
            if (root != null && root.get_Description() != null
                && oid.equals(root.get_Description().getOid())) {
                return root;
            }
        }
        return null;
    }

    /**
     * Gets entity.
     *
     * @param fieldName the field name
     * @return the entity
     */
    public DeviceEntity getEntity(final String fieldName) {
        try {
            final Method method = this.getClass().getMethod(getGetterName(fieldName));
            return (DeviceEntity) method.invoke(this, (Object[]) null);
        } catch (final NoSuchMethodException e) {
            log.warn("Unrecognised field name:{}", fieldName);
            log.trace("Exception:", e);
            throw new InvalidFieldNameException(this, fieldName);
        } catch (final Exception e) {
            log.warn("Exception on get", e);
            throw new FieldAccessMethodException(this, fieldName, e.getMessage());
        }
    }

    @Override
    public DeviceEntityDescription get_Description() {
        return null;
    }

    @SuppressWarnings({"rawtypes" })
    @Override
    public void setObject(final Class clazz, final Object childObject) {
        String sn = null;
        try {
            sn = clazz.getSimpleName();
            final Method method = this.getClass().getMethod(getSetterName(sn), clazz);
            method.invoke(this, childObject);
            if (childObject != null) {
                ((DeviceEntity) childObject).set_ParentEntity(this);
            }
        } catch (final NoSuchMethodException e) {
            log.warn("No such method in setObject. SimpleName:" + sn, e);
            throw new InvalidFieldNameException(this, clazz.getSimpleName());
        } catch (final Exception e) {
            log.warn("Exception in setObject. SimpleName:" + sn, e);
            throw new FieldAccessMethodException(this, sn, e.getMessage());
        }
    }

    @Override
    public void setObject(final Object childObject) {
        setObject(childObject.getClass(), childObject);
    }

    /**
     * Gets object.
     *
     * @param clazz the clazz
     * @return the object
     */
    @SuppressWarnings("rawtypes")
    public Object getObject(final Class clazz) {
        String sn = null;
        try {
            sn = clazz.getSimpleName();
            final Method method = this.getClass().getMethod(getGetterName(sn));
            final Object obj = method.invoke(this);
            return obj;
        } catch (final NoSuchMethodException e) {
            log.warn("No such method in getObject. SimpleName:" + clazz.getSimpleName(), e);
            throw new InvalidFieldNameException(this, clazz.getSimpleName());
        } catch (final Exception e) {
            log.warn("Exception in setObject. SimpleName:" + sn, e);
            throw new FieldAccessMethodException(this, sn, e.getMessage());
        }
    }

    /**
     * Create entity device entity.
     *
     * @param entityName the entity name
     * @return the device entity
     */
    public DeviceEntity createEntity(final String entityName) {
        final Class<? extends DeviceEntity> clazz = getEntityType(entityName);

        final DeviceEntity entity = instantiateObject(clazz);
        setObject(clazz, entity);
        return entity;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DeviceEntity> getEntityType(final String entityName) {
        try {
            return (Class<? extends DeviceEntity>) this.getClass().getMethod(getGetterName(entityName)).getReturnType();
        } catch (final SecurityException e) {
            log.warn("SecurityException on getEntityType", e);
            throw new FieldAccessMethodException(this, entityName, e.getMessage());
        } catch (final NoSuchMethodException e) {
            log.warn("Exception on getEntityType", e);
            throw new InvalidFieldNameException(this, entityName);
        }
    }

    // Instantiates an object of a given class.
    private DeviceEntity instantiateObject(final Class<? extends DeviceEntity> clazz) {
        DeviceEntity object = null;
        try {
            object = clazz.newInstance();
        } catch (final InstantiationException e) {
            log.warn("Failed to instantiate object of type: " + clazz, e);
        } catch (final IllegalAccessException e) {
            log.warn("Failed to access object of type : " + clazz, e);
        }
        return object;
    }

    /**
     * Return the root objects for the device
     *
     * @return an array of {@link DeviceEntity}s each of which is a root of a          hierarchy of entities supported by the device
     */
    public abstract DeviceEntity[] getRoots();
}
