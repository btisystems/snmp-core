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

import org.snmp4j.smi.VariableBinding;

/**
 * A handler of variable bindings returned by an SNMP input operation.
 */
public interface IVariableBindingHandler {

    /**
     * Adds a variable to the device description.
     *
     * @param binding   defines the variable to be added to the description.
     * @return  false only if the binding was not added because the OID was not part
     *          of the device description
     */
    boolean addVariable(VariableBinding binding);

}