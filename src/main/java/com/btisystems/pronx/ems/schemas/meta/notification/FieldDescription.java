package com.btisystems.pronx.ems.schemas.meta.notification;


/**
 * The type Field description.
 */
public class FieldDescription {
    @Override
    public String toString() {
        return "FieldDescription{" +
                "oid='" + oid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldDescription that = (FieldDescription) o;

        if (!oid.equals(that.oid)) {
            return false;
        }
        return !(name != null ? !name.equals(that.name) : that.name != null) && !(description != null ? !description.equals(that.description) : that.description != null) && type == that.type;

    }

    @Override
    public int hashCode() {
        int result = oid.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    /**
     * Gets oid.
     *
     * @return the oid
     */
    public String getOid() {

        return oid;
    }

    /**
     * Sets oid.
     *
     * @param oid the oid
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public FieldType getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * The Oid.
     */
    protected String oid;
    /**
     * The Name.
     */
    protected String name;
    /**
     * The Description.
     */
    protected String description;
    /**
     * The Type.
     */
    protected FieldType type;

    /**
     * With oid field description.
     *
     * @param value the value
     * @return the field description
     */
    public FieldDescription withOid(String value) {
        setOid(value);
        return this;
    }

    /**
     * With name field description.
     *
     * @param value the value
     * @return the field description
     */
    public FieldDescription withName(String value) {
        setName(value);
        return this;
    }

    /**
     * With description field description.
     *
     * @param value the value
     * @return the field description
     */
    public FieldDescription withDescription(String value) {
        setDescription(value);
        return this;
    }

    /**
     * With type field description.
     *
     * @param value the value
     * @return the field description
     */
    public FieldDescription withType(FieldType value) {
        setType(value);
        return this;
    }
}


