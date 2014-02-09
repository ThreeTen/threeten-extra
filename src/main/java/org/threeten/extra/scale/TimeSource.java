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
package org.threeten.extra.scale;

import java.time.Clock;
import java.time.Instant;

/**
 * A clock that provides the current UTC or TAI instant.
 * <p>
 * This clock differs from {@link Clock} in providing access to the current instant
 * in the UTC and TAI time-scales. However, there is currently no implementation that
 * provides accurate UTC or TAI.
 *
 * <h3>Implementation Requirements:</h3>
 * This abstract class must be implemented with care to ensure other classes in
 * the framework operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * <p>
 * The principal methods are defined to allow the throwing of an exception.
 * In normal use, no exceptions will be thrown, however one possible implementation would be to
 * obtain the time from a central time server across the network. Obviously, in this case the
 * lookup could fail, and so the method is permitted to throw an exception.
 * <p>
 * Subclass implementations should implement {@code Serializable} wherever possible.
 * They should also be immutable and thread-safe, implementing {@code equals()},
 * {@code hashCode()} and {@code toString()} based on their state.
 */
public interface TimeSource {

    /**
     * Gets the current {@code Instant}.
     * <p>
     * The instant returned is based on the Java time-scale defined in {@link Instant}.
     * An accurate implementation of this interface will return the correct instant
     * as per that definition.
     *
     * @return the current {@code Instant} from this time-source, not null
     * @throws RuntimeException if the instant cannot be obtained, not thrown by most implementations
     */
    Instant instant();

    /**
     * Gets the current {@code UtcInstant}.
     * <p>
     * The UTC time-scale is the current world civil time and includes leap seconds.
     * An accurate implementation of this interface will return the correct UTC instant.
     *
     * @return the current {@code UtcInstant} from this time-source, not null
     * @throws RuntimeException if the instant cannot be obtained, not thrown by most implementations
     */
    UtcInstant utcInstant();

    /**
     * Gets the current {@code TaiInstant}.
     * <p>
     * The TAI time-scale is a simple incrementing number of seconds from the TAI epoch of 1958-01-01(TAI).
     * It ignores all human concepts of time such as days.
     * An accurate implementation of this interface will return the correct TAI instant.
     *
     * @return the current {@code TaiInstant} from this time-source, not null
     * @throws RuntimeException if the instant cannot be obtained, not thrown by most implementations
     */
    TaiInstant taiInstant();

}
