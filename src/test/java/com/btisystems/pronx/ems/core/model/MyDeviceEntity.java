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
package com.btisystems.pronx.ems.core.model;

import org.junit.Ignore;

/**
 * Small helper class used by the AbstractRootEntityTest class.
 * Note that this class itself contains no tests and is ignored by JUnit.
 */
@Ignore
public class MyDeviceEntity extends DeviceEntity {
    /**
     * Instantiates a new My device entity.
     */
    public MyDeviceEntity() {
    }

    @Ignore
    @Override
    public DeviceEntityDescription get_Description() {
        return null;
    }
}
