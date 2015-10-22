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

import java.util.Date;

import org.snmp4j.PDU;

/**
 * Trap handling service
 */
public interface ITrapHandlerService {

    /**
     * Handle.
     *
     * @param timeRaised the time raised
     * @param address    the address
     * @param trap       the trap
     */
    void handle(Date timeRaised, String address, PDU trap);


}