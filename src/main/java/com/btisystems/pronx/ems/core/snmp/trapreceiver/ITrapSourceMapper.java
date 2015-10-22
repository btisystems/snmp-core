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

package com.btisystems.pronx.ems.core.snmp.trapreceiver;

/**
 * Maps a trap source address to another.
 * For use in testing when generating synthetic notifications.
 */

public interface ITrapSourceMapper {

    /**
     * 
     * @param sourceAddress
     * @return the alternative address to use as the source addresses for a trap.
     */
    String mapAddress(final String sourceAddress);
}