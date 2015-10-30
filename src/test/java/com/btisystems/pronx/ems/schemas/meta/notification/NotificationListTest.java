/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.btisystems.pronx.ems.schemas.meta.notification;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * Test class in addition to {@link NotificationBeansTest}
 */
public class NotificationListTest {
    private NotificationDefinition[] definitions;
    
    public NotificationListTest() {
    }
    
    @Before
    public void setUp() {
        definitions = new NotificationDefinition[]{new NotificationDefinition().withName("Test")};
    }

    @Test
    public void shouldTestWithMethods() {
        final NotificationList notificationList = new NotificationList();
        notificationList.withNotificationDefinition(definitions);
        assertEquals(1, notificationList.getNotificationDefinition().size());
        
        notificationList.withNotificationDefinition(Arrays.asList(definitions));
        assertEquals(2, notificationList.getNotificationDefinition().size());
    }
    
}
