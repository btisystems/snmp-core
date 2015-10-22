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
 * The interface Trap receiver configuration.
 */
public interface ITrapReceiverConfiguration {
    /**
     * Gets trap handler service.
     *
     * @return the trap handler service
     */
    ITrapHandlerService getTrapHandlerService();

    /**
     * Gets address mapper.
     *
     * @return the address mapper
     */
    ITrapSourceMapper getAddressMapper();

    /**
     * Gets dispatcher thread count.
     *
     * @return the dispatcher thread count
     */
    int getDispatcherThreadCount();

    /**
     * Gets listening address.
     *
     * @return the listening address
     */
    String getListeningAddress();
}
