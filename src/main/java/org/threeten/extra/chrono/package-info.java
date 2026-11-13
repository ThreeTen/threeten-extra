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
 * Additional chronologies (calendar systems) that extend {@code java.time.*}.
 * <p>
 * Chronologies and dates follow these principles.
 * <ul>
 * <li><b>Creation is strict.</b>
 *  Every field is validated and an invalid value throws {@code DateTimeException}.
 *  A day that does not exist in its month or year is never adjusted.
 *  The exception to this is {@code prolepticYear}, which permits negative year-of-era values.
 * <li><b>Setting a field and adding an amount follow the same three stages.</b>
 *  <ol>
 *  <li>Validate: When setting, the new value is validated against the outer bounds of the field.
 *   For example, if the day-of-month runs from 1 to 30, but 1-5 in the 13th month, then setting the
 *   day-of-month to 30 is always valid even in the 13th month, but setting it to 31 is always invalid.
 *   When adding, the amount to add may be any positive or negative value.
 *   Validation will check that the result does not exceed the valid range of the date.
 *  <li>Apply: Days and fixed-length weeks are added as days via epoch-days, converting back to a valid date,
 *   potentially in the next month or year.
 *   For example, if the day-of-month runs from 1 to 30, but 1-5 in the 13th month, then setting the
 *   day-of-month to 30 in the 13th month will result in day 25 in the 1st month of the next year.
 *   Larger fields, such as months and years, are added separately, with the smaller fields (days) unresolved
 *   at this point.
 *   For example, if the day-of-month runs from 1 to 30, but 1-5 in the 13th month, then adding one month to
 *   day 30 in the 12th month will calculate (in this stage) day 30 in the 13th month (which is invalid).
 *  <li>Resolve: If the result is not a valid date, the smaller field is clamped to the last valid value of
 *  the larger one. Typically, this is used to resolve a day-of-month that is invalid for the month or year.
 *  If the result exceeds the valid range of the chronology, then an exception is thrown.
 *  </ol>
 *  This is deliberately different from {@code LocalDate.with(DAY_OF_MONTH)}, which validates against the bounds
 *  implied by the base date rather than the outer bounds.
 *  The rationale is that month and year lengths are less well known with these chronologies,
 *  thus this approach is less likely to result in unexpected exceptions.
 * <li><b>{@code minus(n)} gives the same result as {@code plus(-n)}.</b>
 *  Neither is guaranteed to reverse the other when the day is clamped.
 * <li><b>{@code until()} is the inverse of {@code plus()} for standard forward periods.</b>
 *  Adding the result of {@code until()} to the start date gives the end date.
 *  The result may differ when using backward periods.
 * <li><b>Results must be in the supported range.</b>
 *  Otherwise {@code DateTimeException} is thrown.
 *  Numeric overflow may throw {@code ArithmeticException}.
 * </ul>
 */
@NullMarked
package org.threeten.extra.chrono;

import org.jspecify.annotations.NullMarked;
