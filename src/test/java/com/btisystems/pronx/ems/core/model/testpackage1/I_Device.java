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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.btisystems.pronx.ems.core.model.AbstractRootEntity;
import com.btisystems.pronx.ems.core.model.DeviceEntity;
import com.btisystems.pronx.ems.core.model.testpackage1.sub.EntityTest1;

public class I_Device
        extends AbstractRootEntity {

    private EntityTest1 entityTest1;

    public EntityTest1 getEntityTest1() {
        return this.entityTest1;
    }

    public void setEntityTest1(final EntityTest1 test1) {
        final EntityTest1 oldValue = this.entityTest1;
        this.entityTest1 = test1;
        replaceChild(oldValue, test1);
        notifyChange(1, oldValue, test1);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("test1", entityTest1).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(entityTest1).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final I_Device rhs = ((I_Device) obj);
        return new EqualsBuilder().append(entityTest1, rhs.entityTest1).isEquals();
    }

    @Override
    public I_Device clone() {
        final I_Device _copy = new I_Device();
        if (entityTest1 != null) {
            _copy.entityTest1 = entityTest1.clone();
            _copy.entityTest1.set_ParentEntity(_copy);
        }

        return _copy;
    }

    @Override
    public DeviceEntity[] getRoots() {
        return new DeviceEntity[]{entityTest1};
    }

}
