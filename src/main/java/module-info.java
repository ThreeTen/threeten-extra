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

/**
 * ThreeTen-Extra provides additional date-time classes that complement those in Java SE 8.
 * <p>
 * Not every piece of date/time logic is destined for the JDK. Some concepts are too
 * specialized or too bulky to make it in. This project provides some of those additional
 * classes as a well-tested and reliable module.
 */
module org.threeten.extra {

    // only annotations are used, thus they are optional
    requires static org.joda.convert;

    // export all packages
    exports org.threeten.extra;
    exports org.threeten.extra.chrono;
    exports org.threeten.extra.scale;

    // provide the services
    provides java.time.chrono.Chronology
        with org.threeten.extra.chrono.BritishCutoverChronology,
             org.threeten.extra.chrono.CopticChronology,
             org.threeten.extra.chrono.DiscordianChronology,
             org.threeten.extra.chrono.EthiopicChronology,
             org.threeten.extra.chrono.InternationalFixedChronology,
             org.threeten.extra.chrono.JulianChronology,
             org.threeten.extra.chrono.PaxChronology,
             org.threeten.extra.chrono.Symmetry010Chronology,
             org.threeten.extra.chrono.Symmetry454Chronology;

}
