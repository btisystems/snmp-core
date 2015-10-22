package com.btisystems.pronx.ems.schemas.meta.notification;

/**
 * The type Notification meta.
 */
public class NotificationMeta {

    /**
     * The Notification list.
     */
    protected com.btisystems.pronx.ems.schemas.meta.notification.NotificationList notificationList;

    /**
     * The Field list.
     */
    protected FieldList fieldList;

    @Override
    public String toString() {
        return "NotificationMeta{" +
                "notificationList=" + notificationList +
                ", fieldList=" + fieldList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationMeta that = (NotificationMeta) o;

        return !(notificationList != null ? !notificationList.equals(that.notificationList) : that.notificationList != null) && !(fieldList != null ? !fieldList.equals(that.fieldList) : that.fieldList != null);

    }

    @Override
    public int hashCode() {
        int result = notificationList != null ? notificationList.hashCode() : 0;
        result = 31 * result + (fieldList != null ? fieldList.hashCode() : 0);
        return result;
    }

    /**
     * Gets notification list.
     *
     * @return the notification list
     */
    public NotificationList getNotificationList() {

        return notificationList;
    }

    /**
     * Sets notification list.
     *
     * @param notificationList the notification list
     */
    public void setNotificationList(NotificationList notificationList) {
        this.notificationList = notificationList;
    }

    /**
     * Gets field list.
     *
     * @return the field list
     */
    public FieldList getFieldList() {
        return fieldList;
    }

    /**
     * Sets field list.
     *
     * @param fieldList the field list
     */
    public void setFieldList(FieldList fieldList) {
        this.fieldList = fieldList;
    }

    /**
     * With notification list notification meta.
     *
     * @param value the value
     * @return the notification meta
     */
    public NotificationMeta withNotificationList(NotificationList value) {
        setNotificationList(value);
        return this;
    }

    /**
     * With field list notification meta.
     *
     * @param value the value
     * @return the notification meta
     */
    public NotificationMeta withFieldList(FieldList value) {
        setFieldList(value);
        return this;
    }
}
