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
package com.btisystems.pronx.ems.core.snmp;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import org.snmp4j.smi.OID;

/**
 * Class able to look up OID from a given {@link FieldDescription}
 */
public interface ISnmpNotificationOidLookup {

    /**
     * Lookup oid for field oid.
     *
     * @param field to be converted to OID
     * @return the OID value of the field.
     */
    OID lookupOidForField(final FieldDescription field);

}
