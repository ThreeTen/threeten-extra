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
 * An era in the Discordian calendar system.
 * <p>
 * The Discordian calendar system has one era.
 * The current era, for years from 1 onwards, is known as the 'Year of Our Lady of Discord'.
 * No other eras are supported.
 * <p>
 * The start of the Discordian epoch {@code 0001-01-01 (Discordian)} is {@code -1165-01-01 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code DiscordianEra}.
 * Use {@code getValue()} instead.</b>
 *
 * <h3>Implementation Requirements:</h3>
 * This is an immutable and thread-safe enum.
 */
public enum DiscordianEra implements Era {

    /**
     * The singleton instance for the current era, 'Year of Our Lady of Discord',
     * which has the numeric value 1.
     */
    YOLD;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DiscordianEra} from an {@code int} value.
     * <p>
     * {@code DiscordianEra} is an enum representing the Discordian era of YOLD.
     * This factory allows the enum to be obtained from the {@code int} value.
     * Only the value 1 is ever accepted.
     *
     * @param era  the YOLD value to represent, which must be 1 (YOLD)
     * @return the era singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static DiscordianEra of(int era) {
        switch (era) {
            case 1:
                return YOLD;
            default:
                throw new DateTimeException("Invalid era: " + era);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era YOLD has the value 1.
     *
     * @return the era value, 1 for YOLD
     */
    @Override
    public int getValue() {
        return ordinal();
    }

}
