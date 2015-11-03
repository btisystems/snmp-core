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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;


public class NetworkDeviceTest {
    private NetworkDevice device;

    private IClassRegistry registry;

    @Before
    public void setUp() throws Exception {

        registry = createMock(IClassRegistry.class);
        device = new NetworkDevice(registry, "address");
    }

    @Test
    public void shouldIgnoreUnregisteredOids()  {

        final VariableBinding binding = createBinding("1.2.0", "Value");

        expect(registry.getClass(new OID("1.2"))).andReturn(null);
        expect(registry.getClass(new OID("1"))).andReturn(null);

        replayAll();

        device.addVariable(binding);

        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldInstantiateSimpleEntity() {

        final VariableBinding binding1 = createBinding("1.2.0", "Value");
        final VariableBinding binding2 = createBinding("1.3.0", "Value");

        expect(registry.getClass(new OID("1.2"))).andReturn(null);
        expect(registry.getClass(new OID("1"))).andReturn((Class) SimpleEntity.class).times(3);
        expect(registry.getClass(new OID("1.3"))).andReturn(null);
        expect(registry.getClass(new OID("1"))).andReturn((Class) SimpleEntity.class).times(2);
        expect(registry.getRootEntityClass()).andReturn((Class) _Device.class);

        replayAll();

        device.addVariable(binding1);
        device.addVariable(binding2);

        verifyAll();

        assertEquals(SimpleEntity.class, device.getRootObject().getRoots()[0].getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldInstantiateTableEntity() {

        final VariableBinding binding1 = createBinding("1.2.3.1.9.9.9", "Value");
        final VariableBinding binding2 = createBinding("1.2.3.2.9.9.9", "Value");
        final VariableBinding binding3 = createBinding("1.2.3.2.9.9.8", "Value");

        expect(registry.getClass(new OID("1.2.3.1.9.9"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3.1.9"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3.1"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3"))).andReturn((Class) IndexedEntity.class).times(3);

        expect(registry.getClass(new OID("1.2"))).andReturn((Class) TableEntity.class);

        expect(registry.getRootEntityClass()).andReturn((Class) _Device.class);

        expect(registry.getClass(new OID("1.2.3.2.9.9"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3.2.9"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3.2"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3"))).andReturn((Class) IndexedEntity.class).times(3);

        expect(registry.getClass(new OID("1.2.3.2.9.9"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3.2.9"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3.2"))).andReturn(null);
        expect(registry.getClass(new OID("1.2.3"))).andReturn((Class) IndexedEntity.class).times(3);

        replayAll();

        device.addVariable(binding1);
        device.addVariable(binding2);
        device.addVariable(binding3);

        verifyAll();

        assertEquals(TableEntity.class, device.getRootObject().getRoots()[1].getClass());
        assertEquals(2, ((TableEntity) device.getRootObject().getRoots()[1]).getEntries().size());
    }

    private VariableBinding createBinding(final String string, final String string2) {
        final VariableBinding binding = new VariableBinding(new OID(string), new OctetString(string2));
        return binding;
    }

    private void verifyAll() {
        verify(registry);
    }

    private void replayAll() {
        replay(registry);
    }

    static public class _Device extends AbstractRootEntity {

        private static final long serialVersionUID = 1L;
        private SimpleEntity simpleEntity;
        private TableEntity tableEntity;

        @Override
        public DeviceEntity[] getRoots() {
            return new DeviceEntity[] {simpleEntity, tableEntity};
        }

        public void setSimpleEntity(final SimpleEntity entity) {
            this.simpleEntity = entity;
        }

        public void setTableEntity(final TableEntity entity) {
            this.tableEntity = entity;
        }
    }

    static public class SimpleEntity extends DeviceEntity implements IVariableBindingSetter {

        private static final long serialVersionUID = 1L;

        protected String field1;
        protected String field2;

        @Override
        public DeviceEntityDescription get_Description() {
            final DeviceEntityDescription description = new DeviceEntityDescription(new OID("1"));
            description.addField(new DeviceEntityDescription.FieldDescription(1, "field1", DeviceEntityDescription.FieldType.STRING, 5));
            description.addField(new DeviceEntityDescription.FieldDescription(2, "field2", DeviceEntityDescription.FieldType.STRING, 10));
            return description;
        }

        @Override
        public void set(final VariableBinding binding) {

            if (binding.getOid().toString().equals("1.1.0")) {
                field1 = binding.getVariable().toString();
            } else if (binding.getOid().toString().equals("1.2.0")) {
                field2 = binding.getVariable().toString();
            }
        }
    }

    static public class IndexedEntity extends DeviceEntity implements IVariableBindingSetter, IIndexed {

        private static final long serialVersionUID = 1L;

        @Override
        public DeviceEntityDescription get_Description() {
            final DeviceEntityDescription description = new DeviceEntityDescription(new OID("2"));
            description.addField(new DeviceEntityDescription.FieldDescription(1, "dot1agCfmLtrSeqNumber", DeviceEntityDescription.FieldType.UNSIGNED32, -1));
            return description;
        }

        @Override
        public void set(final VariableBinding binding) {
        }

        @Override
        public String _getIndex() {
            return null;
        }

        @Override
        public void _setIndex(final OID oid) {
        }
    }

    static public class TableEntity extends DeviceEntity implements IVariableBindingSetter, ITableAccess<IndexedEntity> {
        private static final long serialVersionUID = 1L;
        @Override
        public DeviceEntityDescription get_Description() {
            final DeviceEntityDescription description = new DeviceEntityDescription(new OID("3"));
            description.addField(new DeviceEntityDescription.FieldDescription(1, "dot1agCfmLtrSeqNumber", DeviceEntityDescription.FieldType.UNSIGNED32, -1));
            return description;
        }

        @Override
        public void set(final VariableBinding binding) {
        }

        private final Map<String, IndexedEntity> entries = new HashMap<String, IndexedEntity>();
        @Override
        public Map<String, IndexedEntity> getEntries() {
            return entries;
        }

        @Override
        public IndexedEntity getEntry(final String index) {
            return entries.get(index);
        }

        @Override
        public void setEntry(final String index, final IndexedEntity entry) {
            entries.put(index, entry);
        }

        @Override
        public IndexedEntity createEntry(final String key) {
            return null;
        }
    }
}

