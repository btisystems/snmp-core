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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps selected trap source address to alternative addresses. For use in
 * testing, when generating 'synthetic' notifications.
 */
public class TrapSourceMapper implements ITrapSourceMapper {

    private static Logger log = LoggerFactory.getLogger(TrapSourceMapper.class);

    private String mapSourceAddress = null;
    private List<String> targetAddressList;
    private int mappedAddressIndex = 0;
    
    private static final String IP_DELIMETER = ".";
    private static final String RANGE_DELIMETER = "-";
    private static final int IP_TOKENS = 4;

    public TrapSourceMapper(final String sourceMapping) {
        log.info("Apply trap source mapping:{}", sourceMapping);

        final String[] tokens = sourceMapping.split(":");
        if (tokens.length == 2) {

            targetAddressList = buildMappedAddressList(tokens[1]);
            if (targetAddressList != null && !targetAddressList.isEmpty()) {
                mapSourceAddress = tokens[0];
            }
        }
    }

    /* (non-Javadoc)
     * @see com.btisystems.pronx.ems.traphandler.ITrapSourceMapper#mapAddress(java.lang.String)
     */
    @Override
    public String mapAddress(final String remoteAddress) {
        if (remoteAddress.equals(mapSourceAddress)) {
            if (mappedAddressIndex == targetAddressList.size()) {
                mappedAddressIndex = 0;
            }
            return targetAddressList.get(mappedAddressIndex++);
        }
        return remoteAddress;
    }

    private List<String> buildMappedAddressList(final String addressListDefinition) {
        log.info("buildMappedAddressList from {}", addressListDefinition);
        final List<String> targetAddresses = new ArrayList<>();
        final String[] tokens = addressListDefinition.split(",");
        for (String token : tokens) {
            final int rangeIndex = token.indexOf('-');
            if (rangeIndex == -1) {
                targetAddresses.add(token);
            } else {
                targetAddresses.addAll(getAddressesInRange(token));
            }
        }

        return targetAddresses;

    }

    private List<String> getAddressesInRange(final String rangeDescription) {
        log.debug("getAddressesInRange from {}", rangeDescription);
        final List<String> rangeAddresses = new ArrayList<>();
        final String[] tokens = rangeDescription.split("\\.");
        if (tokens.length == IP_TOKENS) {
            final String[] startEndTokens = tokens[IP_END_TOKEN].split(RANGE_DELIMETER);
            if (startEndTokens.length == 2) {
                addRangeAddresses(startEndTokens, tokens, rangeAddresses, rangeDescription);
            }
        }
        return rangeAddresses;
    }
    private static final int IP_END_TOKEN = 3;

    private void addRangeAddresses(final String[] startEndTokens, final String[] tokens, final List<String> rangeAddresses, final String rangeDescription) {
        try {
            final int startIndex = Integer.parseInt(startEndTokens[0]);
            final int endIndex = Integer.parseInt(startEndTokens[1]);
            for (int i = startIndex; i <= endIndex; i++) {
                final String address = tokens[0] + IP_DELIMETER
                        + tokens[1] + IP_DELIMETER
                        + tokens[2] + IP_DELIMETER
                        + i;
                log.debug("added address {}", address);
                rangeAddresses.add(address);
            }
        } catch (NumberFormatException e) {
            log.warn("Ignoring invalid range description {}", rangeDescription);
        }
    }
}
