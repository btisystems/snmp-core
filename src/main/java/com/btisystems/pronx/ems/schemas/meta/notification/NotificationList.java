package com.btisystems.pronx.ems.schemas.meta.notification;

import java.util.Collection;
import java.util.List;


/**
 * The type Notification list.
 */
public class NotificationList {
    /**
     * The Notification definition.
     */
    protected List<NotificationDefinition> notificationDefinition;

    @Override
    public String toString() {
        return "NotificationList{" +
                "notificationDefinition=" + notificationDefinition +
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

        NotificationList that = (NotificationList) o;

        return !(notificationDefinition != null ? !notificationDefinition.equals(that.notificationDefinition) : that.notificationDefinition != null);

    }

    @Override
    public int hashCode() {
        return notificationDefinition != null ? notificationDefinition.hashCode() : 0;
    }

    /**
     * Gets notification definition.
     *
     * @return the notification definition
     */
    public List<NotificationDefinition> getNotificationDefinition() {

        return notificationDefinition;
    }

    /**
     * Sets notification definition.
     *
     * @param notificationDefinition the notification definition
     */
    public void setNotificationDefinition(List<NotificationDefinition> notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
    }

    /**
     * With notification definition notification list.
     *
     * @param values the values
     * @return the notification list
     */
    public NotificationList withNotificationDefinition(NotificationDefinition... values) {
        if (values != null) {
            for (NotificationDefinition value : values) {
                getNotificationDefinition().add(value);
            }
        }
        return this;
    }

    /**
     * With notification definition notification list.
     *
     * @param values the values
     * @return the notification list
     */
    public NotificationList withNotificationDefinition(Collection<NotificationDefinition> values) {
        if (values != null) {
            getNotificationDefinition().addAll(values);
        }
        return this;
    }
}
