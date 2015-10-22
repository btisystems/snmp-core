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
package com.btisystems.pronx.ems.schemas.meta.notification;

import java.io.Serializable;

/**
 * The type Notification meta.
 */
public class NotificationMeta implements Serializable {

    private static final long serialVersionUID = -3713352840224003930L;

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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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
