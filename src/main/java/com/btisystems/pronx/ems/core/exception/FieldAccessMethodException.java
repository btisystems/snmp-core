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
package com.btisystems.pronx.ems.core.exception;

import com.btisystems.pronx.ems.core.model.DeviceEntity;

/**
 * Thrown when walk is interrupted by an exception
 */
public class FieldAccessMethodException extends DetailedFaultException {

    private static final long serialVersionUID = -2285401779827218734L;

    /**
     * Instantiates a new Field access method exception.
     *
     * @param entity    the entity
     * @param fieldName the field name
     * @param message   the message
     */
    public FieldAccessMethodException(final DeviceEntity entity, final String fieldName, final String message) {
        super("Unexpected exception accessing field [%2$s] on entity [%1$s] - [%3$s]",
                entity.getClass().getName(), fieldName, message);
    }

}
