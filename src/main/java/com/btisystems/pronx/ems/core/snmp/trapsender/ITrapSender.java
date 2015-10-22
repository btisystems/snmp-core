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
package com.btisystems.pronx.ems.core.snmp.trapsender;

import java.util.List;

import com.btisystems.pronx.ems.core.model.INotification;
import com.btisystems.pronx.ems.core.snmp.ISnmpNotificationOidLookup;

public interface ITrapSender {

    /**
     * Sends the provided notification to all the trap receivers. Any filtering should be done
     * before this stage. The #{@link com.btisystems.pronx.ems.core.model.DeviceEntityDescription}
     * is used to generically build the trap PDU.
     *
     * @param notification   the notification
     * @param lookup         the lookup
     * @param trapRecipients the trap recipients
     */
    void send(INotification notification, final ISnmpNotificationOidLookup lookup, final List<TrapRecipient> trapRecipients);

}
