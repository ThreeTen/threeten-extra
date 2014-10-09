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
 * An era in the Julian calendar system.
 * <p>
 * The Julian calendar system has two eras.
 * The current era, for years from 1 onwards, is known as 'Anno Domini'.
 * All previous years, zero or earlier in the proleptic count or one and greater
 * in the year-of-era count, are part of the 'Before Christ' era.
 * <p>
 * The start of the Julian epoch {@code 0001-01-01 (Julian)} is {@code 0000-12-30 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code JulianEra}.
 * Use {@code getValue()} instead.</b>
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum JulianEra implements Era {

    /**
     * The singleton instance for the era before the current one, 'Before Christ',
     * which has the numeric value 0.
     */
    BC,
    /**
     * The singleton instance for the current era, 'Anno Domini',
     * which has the numeric value 1.
     */
    AD;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JulianEra} from an {@code int} value.
     * <p>
     * {@code JulianEra} is an enum representing the Julian eras of BC/AD.
     * This factory allows the enum to be obtained from the {@code int} value.
     *
     * @param era  the BC/AD value to represent, from 0 (BC) to 1 (AD)
     * @return the era singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static JulianEra of(int era) {
        switch (era) {
            case 0:
                return BC;
            case 1:
                return AD;
            default:
                throw new DateTimeException("Invalid era: " + era);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era BC has the value 0, while the era AD has the value 1.
     *
     * @return the era value, from 0 (BC) to 1 (AD)
     */
    @Override
    public int getValue() {
        return ordinal();
    }

}
