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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.StubberImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import com.btisystems.pronx.ems.core.model.DeviceEntityDescription;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldDescription;
import com.btisystems.pronx.ems.core.model.DeviceEntityDescription.FieldType;
import com.btisystems.pronx.ems.core.snmp.ISnmpConfiguration;
import com.btisystems.pronx.ems.core.snmp.IVariableBindingHandler;
import com.btisystems.pronx.ems.core.snmp.SnmpTableWalker;
import com.btisystems.pronx.ems.core.snmp.WalkResponse;

public class SnmpTableWalkerTest {

	@Mock
    private ISnmpConfiguration mockConfiguration;
	@Mock
    private Session mockSnmpInterface;
	@Mock
    private Target mockTarget;
    @Mock
    private IVariableBindingHandler mockVariableHandler;

    private IpAddress address;

    private SnmpTableWalker tableWalker;
    private ExecutorService executorService;

    private Map<DeviceEntityDescription, List<OID>> currentIndexMap;
    private List<OID> currentIndexList;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        address = new IpAddress("127.0.0.9");

        tableWalker = new SnmpTableWalker(mockConfiguration, mockSnmpInterface, mockTarget, address);
        executorService = Executors.newSingleThreadExecutor();

        when(mockConfiguration.createPDU(PDU.GETBULK)).thenReturn(new PDU()).thenReturn(new PDU());
        when(mockConfiguration.getWalkTimeout()).thenReturn(500);
        when(mockTarget.getVersion()).thenReturn(SnmpConstants.version2c);
        when(mockTarget.getMaxSizeRequestPDU()).thenReturn(4096);
    }

    @After
    public void tearDown() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
    }

    @Test
    public void shouldRetrieveSingleRowFromSingleTable() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap()
        																.withTableEntry(newDeviceEntityDescription("1.2.3"))
        																.withRowIndex("10.11.12")
        																.buildIndexMap();

        configureSnmpInterface().withResponses(
        		new String[] {"1.2.3.1.10.11.12", "VALUE1", "1.2.3.2.10.11.12", "VALUE2", "1.2.3.3.10.11.12", "VALUE3",
        				      "1.2.3.1.10.11.13", "VALUE1", "1.2.3.2.10.11.13", "VALUE2", "1.2.3.3.10.11.13", "VALUE3"});

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.3.10.11.12", "VALUE3"));

        assertThat(walkResponse.isSuccess(), is(true));
        assertThat(walkResponse.getObjectCount(), is(3));
    }

    @Test
    public void shouldRetrieveSingleRowsFromMultipleTables() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes =  newIndexMap()
																		.withTableEntry(newDeviceEntityDescription("1.2.3"))
																		.withRowIndex("10.11.12")
																		.withTableEntry(newDeviceEntityDescription("1.2.4"))
																		.withRowIndex("10.11.12")
																		.buildIndexMap();

        configureSnmpInterface().withResponses(
        		new String[] {"1.2.3.1.10.11.12", "VALUE1", "1.2.3.2.10.11.12", "VALUE2", "1.2.3.3.10.11.12", "VALUE3",
        					  "1.2.4.1.10.11.12", "VALUE1", "1.2.4.2.10.11.12", "VALUE2", "1.2.4.3.10.11.12", "VALUE3",
        		              "1.2.3.1.10.11.13", "VALUE1", "1.2.3.2.10.11.13", "VALUE2", "1.2.3.3.10.11.13", "VALUE3",
                		      "1.2.4.1.10.11.13", "VALUE1", "1.2.4.2.10.11.13", "VALUE2", "1.2.4.3.10.11.13", "VALUE3"});

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.3.10.11.12", "VALUE3"));

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.3.10.11.12", "VALUE3"));

        assertThat(walkResponse.isSuccess(), is(true));
        assertThat(walkResponse.getObjectCount(), is(6));
    }

    @Test
    public void shouldRetrieveMultipleRowsFromMultipleTables() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes =  newIndexMap()
																		.withTableEntry(newDeviceEntityDescription("1.2.3"))
																		.withRowIndex("10.11.12")
																		.withRowIndex("55.66.0")
																		.withTableEntry(newDeviceEntityDescription("1.2.4"))
																		.withRowIndex("10.11.12")
																		.withRowIndex("55.66.0")
																		.buildIndexMap();

        // Using 55.66.0 as index to confirm that lower index limit is computed correctly
        configureSnmpInterface().withResponses(
        		new String[] {"1.2.3.1.10.11.12", "VALUE1", "1.2.3.2.10.11.12", "VALUE2", "1.2.3.3.10.11.12", "VALUE3",
        					  "1.2.4.1.10.11.12", "VALUE1", "1.2.4.2.10.11.12", "VALUE2", "1.2.4.3.10.11.12", "VALUE3",
        		              "1.2.3.1.10.11.13", "VALUE1", "1.2.3.2.10.11.13", "VALUE2", "1.2.3.3.10.11.13", "VALUE3",
                		      "1.2.4.1.10.11.13", "VALUE1", "1.2.4.2.10.11.13", "VALUE2", "1.2.4.3.10.11.13", "VALUE3"},
                new String[] {"1.2.3.1.55.66.0", "VALUE1", "1.2.3.2.55.66.0", "VALUE2", "1.2.3.3.55.66.0", "VALUE3",
        					  "1.2.4.1.55.66.0", "VALUE1", "1.2.4.2.55.66.0", "VALUE2", "1.2.4.3.55.66.0", "VALUE3",
                		      "1.2.3.1.55.66.1", "VALUE1", "1.2.3.2.55.66.1", "VALUE2", "1.2.3.3.55.66.1", "VALUE3",
        					  "1.2.4.1.55.66.1", "VALUE1", "1.2.4.2.55.66.1", "VALUE2", "1.2.4.3.55.66.1", "VALUE3"});


        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.3.10.11.12", "VALUE3"));

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.3.10.11.12", "VALUE3"));


        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.1.55.66.0", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.2.55.66.0", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.3.55.66.0", "VALUE3"));

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.1.55.66.0", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.2.55.66.0", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.4.3.55.66.0", "VALUE3"));

        assertThat(walkResponse.isSuccess(), is(true));
        assertThat(walkResponse.getObjectCount(), is(12));
    }

    @Test
    public void shouldDoNothingIfNoTablesDefined() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap()
        															.withTableEntry(newDeviceEntityDescription("1.2.3"))
        															.buildIndexMap();

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        assertThat(walkResponse.isSuccess(), is(false));
        assertTrue(walkResponse.getThrowable().getMessage().contains("Nothing to retrieve"));
    }

    @Test
    public void shouldDoNothingIfNoIndexesDefined() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap().buildIndexMap();

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        assertThat(walkResponse.isSuccess(), is(false));
        assertTrue(walkResponse.getThrowable().getMessage().contains("Nothing to retrieve"));
    }

    @Test
    public void shouldAbortRetrievalIfSnmpTimesOut() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap()
        																.withTableEntry(newDeviceEntityDescription("1.2.3"))
        																.withRowIndex("10.11.12")
        																.buildIndexMap();

        configureSnmpInterface().withNoResponseInTimeoutPeriod();

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        assertThat(walkResponse.isSuccess(), is(false));
        assertTrue(walkResponse.getThrowable().getMessage().contains("Walk timed out"));
    }

    @Test
    public void shouldAbortRetrievalIfSnmpThrowsAnExcpetion() throws IOException, InterruptedException {

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap()
        																.withTableEntry(newDeviceEntityDescription("1.2.3"))
        																.withRowIndex("10.11.12")
        																.buildIndexMap();

        configureSnmpInterface().withSnmpException("Bad Stuff Happens");

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        assertThat(walkResponse.isSuccess(), is(false));
        assertTrue(walkResponse.getThrowable().getMessage().contains("Bad Stuff Happens"));
    }

    @Test
    public void shouldApplyMaximumColumnsPerPdu() throws IOException, InterruptedException {

        when(mockConfiguration.getMaximumColumnsPerPdu()).thenReturn(1);

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap()
        																.withTableEntry(newDeviceEntityDescription("1.2.3"))
        																.withRowIndex("10.11.12")
        																.buildIndexMap();
        configureSnmpInterface().withResponses(
        		new String[] {"1.2.3.1.10.11.12", "VALUE1"},
        		new String[] {"1.2.3.2.10.11.12", "VALUE2"},
        		new String[] {"1.2.3.3.10.11.12", "VALUE3"},
        		new String[] {"1.2.3.1.10.11.13", "VALUE1"},
        		new String[] {"1.2.3.2.10.11.13", "VALUE2"},
        		new String[] {"1.2.3.3.10.11.13", "VALUE3"}
        		);

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.3.10.11.12", "VALUE3"));

        assertThat(walkResponse.isSuccess(), is(true));
        assertThat(walkResponse.getObjectCount(), is(3));
    }

    @Test
    public void shouldApplyMaximumRowsPerPdu() throws IOException, InterruptedException {

        when(mockConfiguration.getMaximumRowsPerPdu()).thenReturn(1);

        final Map<DeviceEntityDescription, List<OID>> tableIndexes = newIndexMap()
        																.withTableEntry(newDeviceEntityDescription("1.2.3"))
        																.withRowIndex("10.11.12")
        																.buildIndexMap();
        configureSnmpInterface().withResponses(
        		new String[] {"1.2.3.1.10.11.12", "VALUE1", "1.2.3.2.10.11.12", "VALUE2", "1.2.3.3.10.11.12", "VALUE3"},
        		new String[] {"1.2.3.1.10.11.13", "VALUE1", "1.2.3.2.10.11.13", "VALUE2", "1.2.3.3.10.11.13", "VALUE3"}
        		);

        final WalkResponse walkResponse = tableWalker.getTableRows(mockVariableHandler, tableIndexes);

        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.1.10.11.12", "VALUE1"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.2.10.11.12", "VALUE2"));
        verify(mockVariableHandler).addVariable(thatDefinesVariable("1.2.3.3.10.11.12", "VALUE3"));

        assertThat(walkResponse.isSuccess(), is(true));
        assertThat(walkResponse.getObjectCount(), is(3));
    }

	private SnmpTableWalkerTest configureSnmpInterface() {
		return this;
	}

	private SnmpTableWalkerTest withResponses(final String[] ... responseSets) throws IOException {
		Stubber stubber = new StubberImpl();
		for (final String[] responseSet : responseSets) {
			stubber = stubber.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(final InvocationOnMock invocation) throws Throwable {
					final Object[] args = invocation.getArguments();
					final ResponseListener listener = (ResponseListener) args[3];
					final Object userObject = args[2];
					final PDU requestPdu = (PDU) args[0];
//					System.out.println("Got request:" + requestPdu + " maxrep/non-rep:"
//												+ requestPdu.getMaxRepetitions() + "/"
//												+ requestPdu.getNonRepeaters());
					executorService.execute(new Runnable() {
						@Override
						public void run() {
							PDU responsePdu = null;
							for (int i = 0; i < responseSet.length; i += 2) {
								final String responseOid = responseSet[i];
								final String responseValue = responseSet[i + 1];
								if (i == 0) {
									responsePdu = createPdu(responseOid, responseValue);
								} else {
									addPdu(responsePdu, responseOid, responseValue);
								}
							}
							final ResponseEvent response = new ResponseEvent(this, address, requestPdu, responsePdu, userObject);
//							System.out.println("respond:" + responsePdu);
							listener.onResponse(response);
						}
					});
					return null;
				}
			});
		}
		stubber = stubber.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				final Object[] args = invocation.getArguments();
				final ResponseListener listener = (ResponseListener) args[3];
				final Object userObject = args[2];
				final PDU requestPdu = (PDU) args[0];
				System.out.println("Got surplus request:" + requestPdu);
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						final PDU responsePdu = createPdu(null, null);
						final ResponseEvent response = new ResponseEvent(this, address, requestPdu, responsePdu, userObject);
//						System.out.println("respond:" + responsePdu);
						listener.onResponse(response);
					}
				});
				return null;
			}
		});
		stubber.when(mockSnmpInterface).send(isA(PDU.class), same(mockTarget), anyObject(), isA(ResponseListener.class));
		return this;
	}

    private SnmpTableWalkerTest withNoResponseInTimeoutPeriod() throws IOException {
        doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000L);
                        } catch (final InterruptedException ex) {
                            Assert.fail();
                        }
                    }
                });
				return null;
			}
        }).when(mockSnmpInterface).send(isA(PDU.class), same(mockTarget), anyObject(), isA(ResponseListener.class));
        return this;
	}

    private SnmpTableWalkerTest withSnmpException(final String text) throws IOException {
        doThrow(new IOException(text)).when(mockSnmpInterface).send(isA(PDU.class), same(mockTarget), anyObject(), isA(ResponseListener.class));
        return this;
	}

	private VariableBinding thatDefinesVariable(final String variableOid, final String value) {
        return argThat(new ArgumentMatcher<VariableBinding>() {

            @Override
            public boolean matches(final Object argument) {
                final boolean result = ((VariableBinding) argument).getOid().equals(new OID(variableOid))
                    && ((VariableBinding) argument).getVariable().toString().equals(value);
                return result;
            }
        });
    }

    private SnmpTableWalkerTest newIndexMap() {
    	currentIndexMap = new HashMap<DeviceEntityDescription, List<OID>>();
    	return this;
    }

    private SnmpTableWalkerTest withTableEntry(final DeviceEntityDescription entityDescription) {
    	currentIndexList = new ArrayList<OID>();
    	currentIndexMap.put(entityDescription, currentIndexList);
    	return this;
    }

    private SnmpTableWalkerTest withRowIndex(final String indexOid) {
    	currentIndexList.add(new OID(indexOid));
    	return this;
    }

    private Map<DeviceEntityDescription, List<OID>> buildIndexMap() {
    	return currentIndexMap;
    }

	private DeviceEntityDescription newDeviceEntityDescription(final String entityOid) {
		final DeviceEntityDescription description = new DeviceEntityDescription(new OID(entityOid));
		description.addField(new FieldDescription(1, "field1", FieldType.STRING, 10));
		description.addField(new FieldDescription(2, "field2", FieldType.STRING, 10));
		description.addField(new FieldDescription(3, "field3", FieldType.STRING, 10));
		return description;
	}

    private PDU createPdu(final String oid, final String value) {
        final PDU responsePdu = new PDU();
        if (oid != null) {
            addPdu(responsePdu, oid, value);
        }
        return responsePdu;
    }

	private void addPdu(final PDU responsePdu, final String oid, final String value) {
        responsePdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
    }
}
