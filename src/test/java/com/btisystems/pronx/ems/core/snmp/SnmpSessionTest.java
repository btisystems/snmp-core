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

import com.btisystems.pronx.ems.core.exception.SystemObjectIdException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IArgumentMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The type Snmp session test.
 */
public class SnmpSessionTest {

    private static final String SYSTEM_OBJECT_ID_OID = "1.3.6.1.2.1.1.2.0";
    private static final String NE_IP = "9.8.7.6";
    private static final String DUMMY_OID1 = "1.3.6";
    private static final String DUMMY_OID2 = "1.3.6.1.5";
    private static final String PROBLEM_DESCRIPTION = "Bad SNMP stuff happened";

    private ISnmpConfiguration configuration;
    private Session snmpInterface;
    private Target target;
    private IVariableBindingHandler variableHandler;
    private List<OID> oidList;
    private IpAddress address;
    private SnmpSession session;

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = createMock(ISnmpConfiguration.class);
        snmpInterface = createMock(Session.class);
        target = createMock(Target.class);
        variableHandler = createMock(IVariableBindingHandler.class);

        address = new IpAddress(NE_IP);

        session = new SnmpSession(configuration, snmpInterface, target, address);

        oidList = new ArrayList<>();
        oidList.add(new OID(DUMMY_OID1));
    }

    /**
     * Should get host address.
     */
    @Test
    public void shouldGetHostAddress() {
        assertEquals(NE_IP, session.getAddress().getHostAddress());
    }

    /**
     * Should identify device.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldIdentifyDevice() throws IOException {

        final PDU responsePdu = createPdu(SYSTEM_OBJECT_ID_OID, "1.2.555.4");
        final ResponseEvent response = new ResponseEvent(this, address, null, responsePdu, null);

        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());

        final Capture<PDU> pduCapture = new Capture<>();
        expect(snmpInterface.send(capture(pduCapture), same(target))).andReturn(response);

        replayAll();

        final String deviceId = session.identifyDevice();
        assertEquals("1.2.555.4", deviceId);
        assertEquals(SYSTEM_OBJECT_ID_OID, pduCapture.getValue().get(0).getOid().toString());

        verifyAll();
    }

    /**
     * Should throw exception if fail to identify device.
     *
     * @throws IOException the io exception
     */
    @Test(expected = SystemObjectIdException.class)
    public void shouldThrowExceptionIfFailToIdentifyDevice() throws IOException {

        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());

        expect(snmpInterface.send(isA(PDU.class), same(target))).andReturn(createErrorResponseEvent());
        replayAll();

        try {
            session.identifyDevice();
            assert false : "Should not reach";
        } finally {
            verifyAll();
        }
    }

    /**
     * Should throw exception if exception when identify device.
     *
     * @throws IOException the io exception
     */
    @Test(expected = SystemObjectIdException.class)
    public void shouldThrowExceptionIfExceptionWhenIdentifyDevice() throws IOException {

        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());

        expect(snmpInterface.send(isA(PDU.class), same(target))).andThrow(new IOException());
        replayAll();

        try {
            session.identifyDevice();
            assert false : "Should not reach";
        } finally {
            verifyAll();
        }
    }

    /**
     * Should get variable by oid.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldGetVariableByOid() throws IOException {

        final PDU responsePdu = createPdu("1.2.3", "1.2.555.4");
        final ResponseEvent response = new ResponseEvent(this, address, null, responsePdu, null);

        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());

        final Capture<PDU> pduCapture = new Capture<>();
        expect(snmpInterface.send(capture(pduCapture), same(target))).andReturn(response);

        replayAll();

        final String value = session.getVariable("1.2.3");
        assertEquals("1.2.555.4", value);
        assertEquals("1.2.3", pduCapture.getValue().get(0).getOid().toString());

        verifyAll();
    }

    /**
     * Should get variable by oid return int.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldGetVariableByOidReturnInt() throws IOException {
        final PDU responsePdu = createPdu("1", 1);
        final ResponseEvent response = new ResponseEvent(this, address, null, responsePdu, null);

        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());

        final Capture<PDU> pduCapture = new Capture<>();
        expect(snmpInterface.send(capture(pduCapture), same(target))).andReturn(response);

        replayAll();

        final Integer value = session.getVariableAsInt("1");

        Assert.assertEquals(1, value.intValue());

        verifyAll();
    }

    /**
     * Should throw exceptionif fails to get variable by oid.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldThrowExceptionifFailsToGetVariableByOid() throws IOException {
        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());
        expect(snmpInterface.send(isA(PDU.class), same(target))).andThrow(new IOException());

        replayAll();
        final Integer variableAsInt = session.getVariableAsInt("1");

        assertNull(variableAsInt);
        verifyAll();

    }

    /**
     * Should throw exception if fail to get variable.
     *
     * @throws IOException the io exception
     */
    @Test(expected = SystemObjectIdException.class)
    public void shouldThrowExceptionIfFailToGetVariable() throws IOException {

        expect(configuration.createPDU(PDU.GET)).andReturn(new PDU());

        expect(snmpInterface.send(isA(PDU.class), same(target))).andThrow(new IOException());
        replayAll();

        try {
            session.identifyDevice();
            assert false : "Should not reach";
        } finally {
            verifyAll();
        }
    }

    /**
     * Should set variables.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldSetVariables() throws IOException {

        final PDU sendPdu = new PDU();
        sendPdu.setType(PDU.SET);
        expect(configuration.createPDU(PDU.SET)).andReturn(sendPdu);

        final Capture<PDU> pduCapture = new Capture<>();
        expect(snmpInterface.send(capture(pduCapture), same(target))).andReturn(createResponseEvent(createOkPdu()));

        replayAll();

        session.setVariables(getVariableBindings("1.2.3", "Value1",
                "4.5.6", "Value2"));
        verifyAll();

        assertEquals(PDU.SET, pduCapture.getValue().getType());
        assertEquals(2, pduCapture.getValue().getVariableBindings().size());
    }

    /**
     * Should not throw error code excetpion by default.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldNotThrowErrorCodeExcetpionByDefault() throws IOException {
        //Verify the default implementation doesn't throw an exception.
        session.checkErrorCodeAndDescription();
    }

    /**
     * Should throw exception if set variables times out.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldThrowExceptionIfSetVariablesTimesOut() throws IOException {

        final PDU sendPdu = new PDU();
        sendPdu.setType(PDU.SET);
        expect(configuration.createPDU(PDU.SET)).andReturn(sendPdu);

        final Capture<PDU> pduCapture = new Capture<>();
        expect(snmpInterface.send(capture(pduCapture), same(target))).andReturn(createResponseEvent(null));

        replayAll();

        try {
            session.setVariables(getVariableBindings("1.2.3", "Value1",
                    "4.5.6", "Value2"));
        } catch (SnmpIoException sioEx) {
            Assert.assertThat(sioEx.getMessage(),
                    allOf(
                            containsString(NE_IP),
                            containsString("Timed out")
                    ));
        }
        verifyAll();

        assertEquals(PDU.SET, pduCapture.getValue().getType());
        assertEquals(2, pduCapture.getValue().getVariableBindings().size());
    }

    /**
     * Should throw exception if set variables responds in error.
     *
     * @throws IOException the io exception
     */
    @Test
    public void shouldThrowExceptionIfSetVariablesRespondsInError() throws IOException {

        final PDU sendPdu = new PDU();
        sendPdu.setType(PDU.SET);
        expect(configuration.createPDU(PDU.SET)).andReturn(sendPdu);

        final Capture<PDU> pduCapture = new Capture<>();
        expect(snmpInterface.send(capture(pduCapture), same(target))).andReturn(createErrorResponseEvent());

        replayAll();

        try {

            session.setVariables(getVariableBindings("1.2.3", "Value1", "4.5.6", "Value2"));
        } catch (SnmpIoException sioEx) {


            Assert.assertThat(sioEx.getMessage(),
                    allOf(
                            containsString(NE_IP),
                            containsString(PROBLEM_DESCRIPTION)
                    ));
        }
        verifyAll();

        assertEquals(PDU.SET, pduCapture.getValue().getType());
        assertEquals(2, pduCapture.getValue().getVariableBindings().size());
    }

    /**
     * Should walk device.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    @Ignore
    public void shouldWalkDevice() throws IOException, InterruptedException {

        final ExecutorService executorService = Executors.newCachedThreadPool();

        expect(configuration.createPDU(PDU.GETBULK)).andReturn(new PDU());
        expect(target.getVersion()).andReturn(SnmpConstants.version2c);
        expect(configuration.getWalkTimeout()).andReturn(900000).anyTimes();

        expectToGetBulkAndSendResponses(executorService, DUMMY_OID1, DUMMY_OID2, "Value");
        expectToAddVariableBinding(DUMMY_OID2, "Value");

        expectToGetBulkAndSendResponses(executorService, DUMMY_OID2,
                "1.3.6.2.1", "Value2");
        expectToAddVariableBinding("1.3.6.2.1", "Value2");

        expectToGetBulkAndSendResponses(executorService, "1.3.6.2.1",
                "1.3.6.3", "Value3",
                "1.3.6.4", "Value4",
                "1.3.7", "Too Far");
        expectToAddVariableBinding("1.3.6.3", "Value3");
        expectToAddVariableBinding("1.3.6.4", "Value4");

        replayAll();

        final WalkResponse response = session.walkDevice(variableHandler, oidList);
        assertEquals(3, response.getRequestCount()); // TODO Sometimes this fails ... java.lang.AssertionError: expected:<3> but was:<0>
        assertEquals(4, response.getObjectCount());
        assertTrue(response.getWalkTime() >= 0);
        assertNull(response.getThrowable());
        assertTrue(response.toString().contains("success"));

        verifyAll();

        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
    }

    /**
     * Should timout walk device.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void shouldTimoutWalkDevice() throws IOException, InterruptedException {

        final ExecutorService executorService = Executors.newCachedThreadPool();

        expect(configuration.createPDU(PDU.GETBULK)).andReturn(new PDU());
        expect(target.getVersion()).andReturn(SnmpConstants.version2c);
        expect(configuration.getWalkTimeout()).andReturn(1).anyTimes();
        expect(configuration.getMaxRepetitions()).andReturn(1);

        expectToGetBulkAndNotify(executorService, DUMMY_OID1);

        replayAll();

        final WalkResponse response = session.walkDevice(variableHandler, oidList);
        assertFalse(response.isSuccess());
        assertTrue(response.toString().contains("fail"));
        assertNotNull(response.getThrowable());

        verifyAll();
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
    }

    /**
     * Should ignore failure to add variable to device.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void shouldIgnoreFailureToAddVariableToDevice() throws IOException, InterruptedException {

        final ExecutorService executorService = Executors.newCachedThreadPool();

        expect(configuration.createPDU(PDU.GETBULK)).andReturn(new PDU());
        expect(target.getVersion()).andReturn(SnmpConstants.version2c);
        expect(configuration.getWalkTimeout()).andReturn(900000).anyTimes();
        expect(configuration.getMaxRepetitions()).andReturn(1);

        expectToGetBulkAndSendResponses(executorService, DUMMY_OID1, DUMMY_OID2, "Value");
        variableHandler.addVariable(new VariableBinding(new OID(DUMMY_OID2), new OctetString("Value")));
        expectLastCall().andThrow(new UnsupportedOperationException());

        expectToGetBulkAndSendResponses(executorService, DUMMY_OID2, null, null);

        replayAll();

        final WalkResponse response = session.walkDevice(variableHandler, oidList);
        assertEquals(1, response.getRequestCount());
        assertEquals(1, response.getObjectCount());

        verifyAll();

        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
    }

    /**
     * Should throw exception if walk interrupted.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void shouldThrowExceptionIfWalkInterrupted() throws IOException, InterruptedException {

        final ExecutorService executorService = Executors.newCachedThreadPool();

        expect(configuration.createPDU(PDU.GETBULK)).andReturn(new PDU());
        expect(target.getVersion()).andReturn(SnmpConstants.version2c);
        expect(configuration.getWalkTimeout()).andReturn(900000).anyTimes();
        expect(configuration.getMaxRepetitions()).andReturn(1);
        expectToGetBulkAndGenerateInterrupt(executorService, DUMMY_OID1);

        replayAll();

        final WalkResponse response = session.walkDevice(variableHandler, oidList);
        assertFalse(response.isSuccess());
        assertTrue(response.toString().contains("fail"));
        assertNotNull(response.getThrowable());

        verifyAll();
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
    }

    /**
     * Should handle error at end of walk.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void shouldHandleErrorAtEndOfWalk() throws IOException, InterruptedException {

        expect(configuration.createPDU(PDU.GETBULK)).andReturn(new PDU());
        expect(target.getVersion()).andReturn(SnmpConstants.version2c);
        expect(configuration.getMaxRepetitions()).andReturn(1);
        expectToGetBulkAndGenerateException(DUMMY_OID1);

        replayAll();

        final WalkResponse response = session.walkDevice(variableHandler, oidList);
        assertFalse(response.isSuccess());

        verifyAll();
    }

    private void expectToAddVariableBinding(final String oid, final String value) {
        variableHandler.addVariable(new VariableBinding(new OID(oid), new OctetString(value)));
        expectLastCall().andReturn(true);
    }

    private void expectToGetBulkAndSendResponses(final ExecutorService exec,
                                                 final String getOid,
                                                 final String... responses) throws IOException {
        snmpInterface.send(isGetBulkPdu(getOid), same(target), isNull(), isA(ResponseListener.class));
        expectLastCall().andAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                final Object args[] = EasyMock.getCurrentArguments();
                final ResponseListener listener = (ResponseListener) args[3];

                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        PDU responsePdu = null;
                        for (int i = 0; i < responses.length; i += 2) {
                            final String responseOid = responses[i];
                            final String responseValue = responses[i + 1];
                            if (i == 0) {
                                responsePdu = createPdu(responseOid, responseValue);
                            } else {
                                addPdu(responsePdu, responseOid, responseValue);
                            }
                        }
                        final ResponseEvent response = new ResponseEvent(this, address, new PDU(), responsePdu, null);
                        listener.onResponse(response);
                    }
                });
                return null;
            }
        });
        snmpInterface.cancel(isA(PDU.class), isA(ResponseListener.class));
        expectLastCall();
    }

    private void expectToGetBulkAndGenerateInterrupt(final ExecutorService exec,
                                                     final String getOid) throws IOException {
        final Thread waitingThread = Thread.currentThread();
        snmpInterface.send(isGetBulkPdu(getOid), same(target), isNull(), isA(ResponseListener.class));
        expectLastCall().andAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                EasyMock.getCurrentArguments();
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        waitingThread.interrupt();
                    }
                });
                return null;
            }
        });

    }

    private void expectToGetBulkAndNotify(final ExecutorService exec,
                                          final String getOid) throws IOException {
        snmpInterface.send(isGetBulkPdu(getOid), same(target), isNull(), isA(ResponseListener.class));
        expectLastCall().andAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                EasyMock.getCurrentArguments();
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Sleep for long enough so that the thread waiting for the walk times out.
                            //Seems non-deterministic, but will only cause a pause for ~ as long as the timeout,
                            //which is 1 millisecond.
                            Thread.sleep(1000L);
                        } catch (InterruptedException ex) {
                            Assert.fail();
                        }
                    }
                });
                return null;
            }
        });

    }

    private void expectToGetBulkAndGenerateException(final String getOid) throws IOException {
        snmpInterface.send(isGetBulkPdu(getOid), same(target), isNull(), isA(ResponseListener.class));
        expectLastCall().andThrow(new IOException("Something wrong"));
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

    private PDU createPdu(final String oid, final int value) {
        final PDU responsePdu = new PDU();
        if (oid != null) {
            addPdu(responsePdu, oid, value);
        }
        return responsePdu;
    }

    private void addPdu(final PDU responsePdu, final String oid, final int value) {
        responsePdu.add(new VariableBinding(new OID(oid), new Integer32(value)));
    }

    private VariableBinding[] getVariableBindings(final String... valuePairs) {
        final List<VariableBinding> bindings = new ArrayList<>();
        for (int i = 0; i < valuePairs.length; i += 2) {
            bindings.add(new VariableBinding(new OID(valuePairs[i]), new OctetString(valuePairs[i + 1])));
        }

        return bindings.toArray(new VariableBinding[bindings.size()]);
    }

    private PDU createOkPdu() {
        return new PDU();
    }

    private PDU createErrorPdu() {
        final PDU errorPdu = new PDU();
        errorPdu.setErrorStatus(SnmpConstants.SNMP_ERROR_BAD_VALUE);
        errorPdu.setErrorIndex(1);
        errorPdu.add(new VariableBinding(new OID("1.2.3"), new OctetString("Bad Value")));
        return errorPdu;
    }

    private ResponseEvent createResponseEvent(final PDU responsePdu) {
        return new ResponseEvent(this, address, null, responsePdu, null);
    }

    private ResponseEvent createErrorResponseEvent() {
        return new ResponseEvent(new Snmp(), address, null, null, null, new IOException(PROBLEM_DESCRIPTION));
    }

    private void replayAll() {
        replay(configuration);
        replay(snmpInterface);
        replay(target);
        replay(variableHandler);
    }

    private void verifyAll() {
        verify(configuration);
        verify(snmpInterface);
        verify(target);
        verify(variableHandler);
    }

    private PDU isGetBulkPdu(final String getOid) {
        reportMatcher(new PduMatcher(PDU.GETBULK, getOid));
        return null;
    }

    private static class PduMatcher implements IArgumentMatcher {

        private final String oid;
        private String actualOid;
        private final int type;

        /**
         * Instantiates a new Pdu matcher.
         *
         * @param type the type
         * @param oid  the oid
         */
        public PduMatcher(final int type, final String oid) {
            this.oid = oid;
            this.type = type;
        }

        public void appendTo(final StringBuffer sb) {
            sb.append("Oid Expected:" + oid + " Actual:" + actualOid);
        }

        public boolean matches(final Object o) {
            final PDU pdu = (PDU) o;
            actualOid = pdu.get(0).getOid().toString();
            return pdu.getType() == type && actualOid.equals(oid);
        }
    }
}
