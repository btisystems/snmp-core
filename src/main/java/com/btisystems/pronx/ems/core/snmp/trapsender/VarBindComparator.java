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
package com.btisystems.pronx.ems.core.snmp.trapsender;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Used to order Var Binds in accordance with Snmp standards, i.e. in alpha
 * numeric order by OID.
 */
public class VarBindComparator implements Serializable, Comparator<String> {

    private static final int DIGIT_LOWER_BOUND = 48;
    private static final int DIGIT_UPPER_BOUND = 57;
    private static final long serialVersionUID = -5662537156155610845L;

    @Override
    public int compare(final String o1, final String o2) {
        final String s1 = o1;
        final String s2 = o2;

        int thisMarker = 0;
        int thatMarker = 0;
        final int s1Length = s1.length();
        final int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            final String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            final String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                final int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0) {
                return result;
            }
        }
        return s1Length - s2Length;
    }

    private String getChunk(final String s, final int slength, final int marker) {
        int chunkMarker = marker;
        final StringBuilder chunk = new StringBuilder();
        final char c = s.charAt(chunkMarker);
        chunk.append(c);
        chunkMarker++;
        if (isDigit(c)) {
            orderDigit(chunkMarker, slength, s, chunk);
        } else {
            orderNonDigit(chunkMarker, slength, s, chunk);
        }
        return chunk.toString();
    }

    private boolean isDigit(final char ch) {
        return ch >= DIGIT_LOWER_BOUND && ch <= DIGIT_UPPER_BOUND;
    }

    private void orderDigit(final int chunkMarker, final int slength, final String s, final StringBuilder chunk) {
        int cm = chunkMarker;
        char c;
        while (cm < slength) {
            c = s.charAt(chunkMarker);
            if (!isDigit(c)) {
                break;
            }
            chunk.append(c);
            cm++;
        }
    }

    private void orderNonDigit(final int chunkMarker, final int slength, final String s, final StringBuilder chunk) {
        int cm = chunkMarker;
        char c;
        while (cm < slength) {
            c = s.charAt(chunkMarker);
            if (isDigit(c)) {
                break;
            }
            chunk.append(c);
            cm++;
        }
    }

}
