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

/**
 * The type Device.
 */
@SuppressWarnings("checkstyle:typename")
public class I_Device extends AbstractRootEntity {

    @Override
    public String toString() {
        return "";
    }

    @Override
    @SuppressWarnings("checkstyle:superclone")
    public I_Device clone() {
        return new I_Device();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        return true;
    }

    @Override
    public DeviceEntity[] getRoots() {
        return new DeviceEntity[0];
    }
}
