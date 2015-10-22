package com.btisystems.pronx.ems.schemas.meta.notification;


/**
 * The enum Field type.
 */
public enum FieldType {

    /**
     * Integer field type.
     */
    INTEGER,
    /**
     * String field type.
     */
    STRING;

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /**
     * From value field type.
     *
     * @param v the v
     * @return the field type
     */
    public static FieldType fromValue(String v) {
        return valueOf(v);
    }

}