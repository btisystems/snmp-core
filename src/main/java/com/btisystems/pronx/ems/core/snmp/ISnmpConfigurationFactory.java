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

/**
 * Factory for the creation of {@link ISnmpConfiguration}s.
 */
public interface ISnmpConfigurationFactory {
    /**
     * Delivers an {@link ISnmpConfiguration}. These may differ based on the
     * {@link AccessType} by using, for example, different Community values.
     *
     * @param accessType defines type of access required
     * @return the configuration
     */
    ISnmpConfiguration getConfiguration(AccessType accessType);

    /**
     * The enum Access type.
     */
    public enum AccessType {
        /**
         * Read only access type.
         */
        READ_ONLY, /**
         * Read write access type.
         */
        READ_WRITE};
}