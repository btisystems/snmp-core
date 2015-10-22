package com.btisystems.pronx.ems.schemas.meta.notification;


import java.util.Collection;
import java.util.List;

/**
 * The type Field list.
 */
public class FieldList {
    /**
     * The Field description.
     */
    protected List<FieldDescription> fieldDescription;

    @Override
    public String toString() {
        return "FieldList{" +
                "fieldDescription=" + fieldDescription +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldList fieldList = (FieldList) o;

        return !(fieldDescription != null ? !fieldDescription.equals(fieldList.fieldDescription) : fieldList.fieldDescription != null);

    }

    @Override
    public int hashCode() {
        return fieldDescription != null ? fieldDescription.hashCode() : 0;
    }

    /**
     * Gets field description.
     *
     * @return the field description
     */
    public List<FieldDescription> getFieldDescription() {

        return fieldDescription;
    }

    /**
     * Sets field description.
     *
     * @param fieldDescription the field description
     */
    public void setFieldDescription(List<FieldDescription> fieldDescription) {
        this.fieldDescription = fieldDescription;
    }


    /**
     * With field description field list.
     *
     * @param values the values
     * @return the field list
     */
    public FieldList withFieldDescription(FieldDescription... values) {
        if (values!= null) {
            for (FieldDescription value: values) {
                getFieldDescription().add(value);
            }
        }
        return this;
    }

    /**
     * With field description field list.
     *
     * @param values the values
     * @return the field list
     */
    public FieldList withFieldDescription(Collection<FieldDescription> values) {
        if (values!= null) {
            getFieldDescription().addAll(values);
        }
        return this;
    }


}
