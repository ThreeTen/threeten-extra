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
 *  <p>
 *  There are two exceptions:
 *  Firstly, {@code prolepticYear} permits negative year-of-era values, as required for lenient parsing.
 *  Secondly, where days are missing within a month (rather than at the end of a month), the invalid day
 *  is adjusted relative to the previous valid day (leniently).
 *  For example, with a gap where day 5 and day 6 are missing, a request to create day 5 will result in day 7,
 *  a request to create day 6 will result in day 8, but a request to create day 7 will result in day 7 (as it is valid).
 * <li><b>Setting a field and adding an amount follow the same three stages.</b>
 *  <ol>
 *  <li>Validate: When setting, the new value is validated against the outer bounds of the field.
 *   For example, if the day-of-month runs from 1 to 30, but 1-5 in the 13th month, then setting the
 *   day-of-month to 30 is always valid even in the 13th month, but setting it to 31 is always invalid.
 *   When adding, the amount to add may be any positive or negative value.
 *   Validation will check that the result does not exceed the valid range of the date.
 *  <li>Determine field groups: Given the chronology, fields are grouped into sets that are applied together.
 *   In most cases, the sets are months and larger, and days and smaller.
 *   These sets are driven by the variable number of days in a month. If a chronology has a variable
 *   number of months in a year as well, then there would be three sets: years, months, and days.
 *  <li>Apply by field group: Each field group is applied in turn from largest to smallest.
 *   Between each group, the smaller field groups are resolved to create a valid date.
 *   Where there are two field groups, this results in four sub-stages:
 *   <ol>
 *   <li>Add years and months
 *   <li>Resolve the resulting date to a valid date (previous valid day-of-month)
 *   <li>Add days
 *   <li>Resolve the resulting date to a valid date (lenient overflow to next month)
 *   </ol>
 *   For example, if the day-of-month runs from 1 to 30, but 1-5 in the 13th month, then adding one month to day 30 in
 *   the 12th month will initially result in day 30 in the 13th month (which is invalid). At sub-stage 2, the result
 *   is resolved to day 5 in the 13th month.
 *   <p>
 *   If adding a period that contains both months and days, the order of operations is important.
 *   For example, adding one month and one day to day 30 in the 12th month will initially result in day 30 in the
 *   13th month (which is invalid). At sub-stage 2, the result is resolved to day 5 in the 13th month.
 *   At sub-stage 3, one day is added to day 5 in the 13th month, resulting in day 6 in the 13th month.
 *   At sub-stage 4, the result is resolved to day 1 in the 1st month of the next year.
 *   If the result exceeds the valid range of the chronology, then an exception is thrown.
 *   <p>
 *   Where there are three field groups, the process is similar, but with additional sub-stages for the extra
 *   field group, with the extra resolve stage using "previous valid" logic.
 *  </ol>
 *  Stage 1 is deliberately different from {@code LocalDate.with(DAY_OF_MONTH)}, which validates against the
 *  bounds implied by the base date rather than the outer bounds.
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
