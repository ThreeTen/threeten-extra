/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.threeten.extra.chrono;

import java.time.DateTimeException;
import java.time.chrono.Era;

/**
 * An era in the International Fixed calendar system.
 * <p>
 * The International Fixed calendar system only has one era.
 * The current era, for years from 1 onwards, is known as 'Current Era'.
 * All previous years are invalid.
 * <p>
 * The start of the International Fixed epoch {@code 0001/01/01 (IFC)} is {@code 0001-01-01 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code InternationalFixedEra}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum InternationalFixedEra implements Era {
    /**
     * The singleton instance for the current era, 'Current Era',
     * which has the numeric value 1.
     */
    CE;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code InternationalFixedEra} from an {@code int} value.
     * <p>
     * {@code InternationalFixedEra} is an enum representing the International Fixed era of CE.
     * This factory allows the enum to be obtained from the {@code int} value.
     *
     * @param era the CE value to represent, only 1 (CE)
     * @throws DateTimeException if the value is invalid
     * @return the era singleton, not null
     */
    public static InternationalFixedEra of(final int era) {
        if (1 == era) {
            return CE;
        }

        throw new DateTimeException("Invalid era: " + era);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era CE has the value 1.
     *
     * @return the era value, only 1 (CE)
     */
    @Override
    public int getValue() {
        return ordinal();
    }
}
