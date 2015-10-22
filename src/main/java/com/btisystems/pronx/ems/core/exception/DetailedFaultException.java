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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all service exceptions.
 */
public abstract class DetailedFaultException extends RuntimeException {

    private static final long serialVersionUID = 3666677767195443650L;

    /**
     * Logger - available to sub-classes
     */
    private static final Logger logger = LoggerFactory.getLogger(DetailedFaultException.class);
    private final String text;
    private final String[] variables;

    /**
     * Construct and log a service exception
     *
     * @param text      the error text
     * @param variables variable for the text.
     */
    public DetailedFaultException(final String text, final String... variables) {
        super(format(text, variables));
        this.text = text;
        this.variables = variables;

        logger.error(format(text, variables));
    }

    /**
     * Format text and variables
     */
    private static String format(final String text, final String... variables) {
        return String.format(text, (Object[]) variables);
    }

    /**
     * Construct and log a service exception
     *
     * @param cause     the cause
     * @param text      the error text
     * @param variables variable for the text.
     */
    public DetailedFaultException(final Throwable cause, final String text, final String... variables) {
        super(format(text, variables), cause);
        this.text = text;
        this.variables = variables;
        if (logger.isErrorEnabled()) {
            logger.error(format(text, variables), cause);
        }
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Get variables string [ ].
     *
     * @return the string [ ]
     */
    public String[] getVariables() {
        return variables;
    }
}
