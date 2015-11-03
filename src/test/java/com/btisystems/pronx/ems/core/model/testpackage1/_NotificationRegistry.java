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
package com.btisystems.pronx.ems.core.model.testpackage1;

import java.util.HashMap;

import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.core.model.NotificationDescription;

public class _NotificationRegistry {

    public final static HashMap<OID, NotificationDescription> notificationOidRegistry = new HashMap<OID, NotificationDescription>();
    public final static HashMap<String, NotificationDescription> notificationNameRegistry = new HashMap<String, NotificationDescription>();

    static {
        register(new NotificationDescription(new OID("1.3.6.1.4.1.18070.2.5.10.1"), "port1linkup", "when the port link status form down to up, the device will send the trap to snmp trap server", null));
        register(new NotificationDescription(new OID("1.3.6.1.4.1.18070.2.5.10.2"), "port1linkdown", "when the port link status form up to down, the device will send the trap to snmp trap server", null));
        register(new NotificationDescription(new OID("1.3.6.1.4.1.18070.2.5.10.3"), "port2linkup", "Description.", null));
        register(new NotificationDescription(new OID("1.3.6.1.4.1.18070.2.5.10.4"), "port2linkdown", "Description.", null));
    }

    private final static void register(final NotificationDescription description) {
        notificationOidRegistry.put(description.getOid(), description);
        notificationNameRegistry.put(description.getName(), description);
    }

}
