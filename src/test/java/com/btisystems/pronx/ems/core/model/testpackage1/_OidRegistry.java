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

import java.util.TreeMap;

import org.snmp4j.smi.OID;

import com.btisystems.pronx.ems.core.model.DeviceEntity;
import com.btisystems.pronx.ems.core.model.testpackage1.sub.EntityTest1;
import com.btisystems.pronx.ems.core.model.testpackage1.sub.EntityTest2;
import com.btisystems.pronx.ems.core.model.testpackage1.sub.TableEntityTest;
import com.btisystems.pronx.ems.core.model.testpackage1.sub.TableEntryTest;

public class _OidRegistry {

    public final static TreeMap<OID, Class<? extends DeviceEntity>> oidRegistry = createMap();

    private static TreeMap<OID, Class<? extends DeviceEntity>> createMap() {
        final TreeMap<OID, Class<? extends DeviceEntity>> map = new TreeMap<OID, Class<? extends DeviceEntity>>();
        map.put(new OID("1.3.6.1.2.1.1"), EntityTest1.class);
        map.put(new OID("1.3.6.1.2.1.1.1"), EntityTest1.class);
        map.put(new OID("1.3.6.1.2.1.1.1.1"), EntityTest1.class);
        map.put(new OID("1.3.6.1.2.1.1.2"), EntityTest1.class);
        map.put(new OID("1.3.6.1.2.1.1.2.1"), EntityTest1.class);
        map.put(new OID("1.3.6.1.2.1.2"), EntityTest2.class);
        map.put(new OID("1.3.6.1.2.2.3"), TableEntityTest.class);
        map.put(new OID("1.3.6.1.2.2.3.1"), TableEntryTest.class);
        return map;
    }

}
