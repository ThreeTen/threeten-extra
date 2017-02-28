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
 * An era in the FrenchRepublic calendar system.
 * <p>
 * The French Republican calendar system has only one official era for years from 1 onwards.
 * All previous years, zero or earlier, are part of the 'Before Republican' era.
 * <p>
 * The start of the French Republican epoch {@code 0001-01-01 (French Republican)} is {@code 1792-09-22 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code FrenchRepublicEra}.
 * Use {@code getValue()} instead.</b>
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum FrenchRepublicEra implements Era {

    /**
     * The singleton instance for the era before the republican era
     * which has the numeric value 0.
     */
    BEFORE_REPUBLICAN,
    /**
     * The singleton instance for the republican era, starting at the gregorian
     * date 22 septembre 1792
     * which has the numeric value 1.
     */
    REPUBLICAN;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code FrenchRepublicEra} from an {@code int} value.
     * <p>
     * {@code FrenchRepublicEra} is an enum representing the FrenchRepublic eras of BEFORE_REPUBLICAN/REPUBLICAN.
     * This factory allows the enum to be obtained from the {@code int} value.
     *
     * @param era  the BEFORE_REPUBLICAN/REPUBLICAN value to represent, from 0 (BEFORE_REPUBLICAN) to 1 (REPUBLICAN)
     * @return the era singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static FrenchRepublicEra of(int era) {
        switch (era) {
            case 0:
                return BEFORE_REPUBLICAN;
            case 1:
                return REPUBLICAN;
            default:
                throw new DateTimeException("Invalid era: " + era);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era BEFORE_REPUBLICAN has the value 0, while the era REPUBLICAN has the value 1.
     *
     * @return the era value, from 0 (BEFORE_REPUBLICAN) to 1 (REPUBLICAN)
     */
    @Override
    public int getValue() {
        return ordinal();
    }

}
