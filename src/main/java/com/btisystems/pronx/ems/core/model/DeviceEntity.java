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
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class to be extended by the root entity for each device type.
 */
public abstract class DeviceEntity implements IDeviceEntity, Serializable {
    /**
     * The constant EMPTY_STRING.
     */
    public static final String EMPTY_STRING = "";
    /**
     * The constant DOT.
     */
    public static final String DOT = ".";
    /**
     * The constant S_02X.
     */
    public static final String S_02X = "%s%02x";
    /**
     * The constant COLON.
     */
    public static final String COLON = ":";
    private static final Logger LOG = LoggerFactory.getLogger(DeviceEntity.class);
    private final Set<PropertyChangeListener> changeListeners = new HashSet<>();

    /**
     * Gets children.
     *
     * @return the children
     */
    @SuppressWarnings("unchecked")
    Collection<DeviceEntity> getChildren() {
        final Collection<DeviceEntity> children = new ArrayList<>();
        if (get_Description() != null) {
            addFields(children);
        }
        return children;
    }

    private void addFields(final Collection<DeviceEntity> children) {
        for (final FieldDescription field : get_Description().getFields()) {
            addField(field, children);
        }
    }

    private void addField(final FieldDescription field, final Collection<DeviceEntity> children) {
        if (field.getType() == FieldType.ENTITY) {
            addEntity(field, children);
        } else if (field.getType() == FieldType.TABLE) {
            addTable(children);
        }
    }

    @SuppressWarnings("unchecked")
    private void addTable(final Collection<DeviceEntity> children) {
        final ITableAccess tableAccess = (ITableAccess) this;
        children.addAll(tableAccess.getEntries().values());
    }

    private void addEntity(final FieldDescription field, final Collection<DeviceEntity> children) {
        final Object obj = invokeGetter(field.getName());
        if (obj instanceof DeviceEntity) {
            children.add((DeviceEntity) obj);
        }
    }

    /**
     * Add child.
     *
     * @param child the child
     */
    protected void addChild(final IDeviceEntity child) {
        for (final PropertyChangeListener listener : changeListeners) {
            child.addPropertyChangeListener(listener);
        }
    }

    /**
     * Remove child.
     *
     * @param child the child
     */
    protected void removeChild(final DeviceEntity child) {
        for (final PropertyChangeListener listener : changeListeners) {
            child.removePropertyChangeListener(listener);
        }
    }

    /**
     * Replace child.
     *
     * @param oldChild the old child
     * @param newChild the new child
     */
    protected void replaceChild(final DeviceEntity oldChild,
                                final DeviceEntity newChild) {
        if (oldChild != null) {
            removeChild(oldChild);
        }
        if (newChild != null) {
            addChild(newChild);
        }
    }

    /**
     * Notify change.
     *
     * @param fieldId  the field id
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void notifyChange(final int fieldId, final Object oldValue, final Object newValue) {
        for (final PropertyChangeListener listener : changeListeners) {
            listener.propertyChange(new PropertyChangeEvent(this, EMPTY_STRING + Integer.toString(fieldId), oldValue, newValue));
        }
    }

    /**
     * Deliver an object identifier string from part of an integer array.
     *
     * @param intArray the source array
     * @param offset   the offset of the first integer of the identifier
     * @param length   the number of sub-identifiers in the identifier
     * @return a string of the form "n1.n2....nn", where n1 is the                  
     * integer at intArray[offset], n2 is the is the integer at                  
     * intArray[offset + 1] and nn is the integer at                  
     * intArray[offset + n - 1]
     */
    protected String _getObjectIdentifier(final int[] intArray,
                                          final int offset,
                                          final int length) {
        final StringBuilder buffer = new StringBuilder();

        String sepChar = EMPTY_STRING;
        for (int x = 0; x < length; x++) {
            buffer.append(sepChar).append(intArray[offset + x]);
            sepChar = DOT;
        }
        return buffer.toString();
    }

    /**
     * Deliver a Mac address string from part of an integer array.
     *
     * @param intArray the source array
     * @param offset   the offset of the first integer of the identifier
     * @param length   the number of sub-identifiers in the identifier
     * @return a string of the form "n1.n2....nn", where n1 is the                  integer at intArray[offset], n2 is the is the integer at                  intArray[offset + 1] and nn is the integer at                  intArray[offset + n - 1]
     */
    protected String _getMacAddress(final int[] intArray,
                                    final int offset,
                                    final int length) {
        final StringBuilder buffer = new StringBuilder();

        String sepChar = EMPTY_STRING;
        for (int x = 0; x < length; x++) {
            buffer.append(String.format(S_02X, sepChar, intArray[offset + x]));
            sepChar = COLON;
        }
        return buffer.toString();
    }

    @Override
    public Object clone() { // NO-CHECKSTYLE
        LOG.warn("clone not implemented for {}", this);
        return null;
    }

    @Override
    public String toString() {
        return "DeviceEntity{" + "changeListeners=" + changeListeners + ", get_Description()=" + get_Description() + '}';
    }

    @Override
    public abstract DeviceEntityDescription get_Description();

    @Override
    public String getString(final String fieldName) {
        return (String) invokeGetter(fieldName);
    }

    @Override
    public int getInt(final String fieldName) {
        return (Integer) invokeGetter(fieldName);
    }

    @Override
    public long getLong(final String fieldName) {
        return (Long) invokeGetter(fieldName);
    }

    @Override
    public void set(final String fieldName, final String value) {
        invokeSetter(fieldName, value, String.class);
    }

    @Override
    public void set(final String fieldName, final int value) {
        invokeSetter(fieldName, value, int.class);
    }

    @Override
    public void set(final String fieldName, final long value) {
        invokeSetter(fieldName, value, long.class);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeListeners.add(listener);
        for (final DeviceEntity child : getChildren()) {
            child.addPropertyChangeListener(listener);
        }
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeListeners.remove(listener);
        for (final DeviceEntity child : getChildren()) {
            child.removePropertyChangeListener(listener);
        }
    }

    @Override
    public void clearPropertyChangeListeners() {
        changeListeners.clear();
        for (final DeviceEntity child : getChildren()) {
            child.clearPropertyChangeListeners();
        }
    }

    @Override
    public void set_ParentEntity(final AbstractRootEntity parent) {

    }

    @Override
    public boolean isSupported(final String fieldName) {
        final DeviceEntityDescription description = get_Description();
        return description.getFieldByName(fieldName) != null;
    }

    @SuppressWarnings("unchecked")
    private void invokeSetter(final String fieldName, final Object value, final Class type) {
        try {
            final Method method = this.getClass().getMethod(getSetterName(fieldName), type);
            method.invoke(this, value);
        } catch (final NoSuchMethodException e) {
            LOG.warn("Exception on getMethod", e);
            throw new InvalidFieldNameException(this, fieldName);
        } catch (final Exception e) {
            LOG.warn("Exception on set", e);
            throw new FieldAccessMethodException(this, fieldName, e.getMessage());
        }
    }

    /**
     * Gets setter name.
     *
     * @param field the field
     * @return the setter name
     * @throws java.lang.Exception
     */
    protected String getSetterName(final String field) throws Exception {
        return "set" + capitalizeFirstCharacter(field);
    }

    private Object invokeGetter(final String fieldName) {
        try {
            final Method method = this.getClass().getMethod(getGetterName(fieldName));
            return method.invoke(this, (Object[]) null);
        } catch (final NoSuchMethodException e) {
            LOG.warn("Exception on getMethod", e);
            throw new InvalidFieldNameException(this, fieldName);
        } catch (final Exception e) {
            LOG.warn("Exception on get", e);
            throw new FieldAccessMethodException(this, fieldName, e.getMessage());
        }
    }

    /**
     * Gets getter name.
     *
     * @param field the field
     * @return the getter name
     */
// Get name for get accessor method.
    protected String getGetterName(final String field) {
        return "get" + capitalizeFirstCharacter(field);
    }

    // Capitalise first character of the specified name.
    private String capitalizeFirstCharacter(final String name) {
        final StringBuilder sb = new StringBuilder(name);
        final char firstChar = sb.charAt(0);
        sb.setCharAt(0, Character.toUpperCase(firstChar));
        return sb.toString();
    }

    /**
     * Get change listeners set.
     *
     * @return the set
     */
    Set<PropertyChangeListener> _getChangeListeners() {
        return changeListeners;
    }
}
