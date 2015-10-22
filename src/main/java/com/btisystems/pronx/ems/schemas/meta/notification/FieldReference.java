package com.btisystems.pronx.ems.schemas.meta.notification;


/**
 * The type Field reference.
 */
public class FieldReference {
    /**
     * The Description.
     */
    protected Object description;

    @Override
    public String toString() {
        return "FieldReference{" +
                "description=" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldReference that = (FieldReference) o;

        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public Object getDescription() {

        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(Object description) {
        this.description = description;
    }

    /**
     * With description field reference.
     *
     * @param value the value
     * @return the field reference
     */
    public FieldReference withDescription(Object value) {
        setDescription(value);
        return this;
    }

}
