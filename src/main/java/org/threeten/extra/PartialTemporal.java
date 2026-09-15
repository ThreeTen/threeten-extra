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
package org.threeten.extra;

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

/**
 * An incomplete "partial" date and/or time to be stored.
 * <p>
 * {@code PartialTemporal} is an immutable date-time object that represents an arbitrary set of date and/or time fields.
 * It can be thought of as primarily a map of {@link TemporalField} to {@code long} values, plus an optional {@link ZoneId}.
 * <p>
 * There are no constraints on the fields that can be stored, and no constraints on the values of those fields.
 * For example, you could store the month as -5,000 if desired, or you could store the combination
 * {@code DAY_OF_YEAR} 1 and {@code MONTH_OF_YEAR} 2, which cannot be resolved to a date.
 * <p>
 * The main purpose of this class is to allow the set of fields to be stored.
 * If desired, the fields can be resolved into a {@link TemporalAccessor} using the {@link #resolve(ResolverStyle)} method.
 * This performs the same logic that a {@link DateTimeFormatter} would use when parsing a string.
 * <p>
 * The class operates in the ISO chronology.
 *
 * <h3>Implementation Requirements:</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class PartialTemporal implements TemporalAccessor {

    /**
     * The parsed fields.
     */
    private final TreeMap<TemporalField, Long> fieldValues;
    /**
     * The parsed zone.
     */
    private final @Nullable ZoneId zone;

    /**
     * Obtains an empty instance.
     * <p>
     * This effectively puts no constraints on the date/time, and can be used to represent any date/time.
     *
     * @return the empty partial temporal
     */
    public static PartialTemporal empty() {
        return new PartialTemporal(createMap(), null);
    }

    /**
     * Obtains an instance from a single field-value, with no time-zone.
     *
     * @param field the field
     * @param value the value
     * @return the partial temporal
     */
    public static PartialTemporal of(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        TreeMap<TemporalField, Long> map = createMap();
        map.put(field, value);
        return new PartialTemporal(map, null);
    }

    /**
     * Obtains an instance from a map of field-values, with no time-zone.
     *
     * @param fieldValues the field-values
     * @return the partial temporal
     */
    public static PartialTemporal of(Map<TemporalField, Long> fieldValues) {
        Objects.requireNonNull(fieldValues, "fieldValues");
        TreeMap<TemporalField, Long> map = createMap();
        map.putAll(fieldValues);
        return new PartialTemporal(map, null);
    }

    /**
     * Obtains an instance from a map of field-values and a time-zone.
     *
     * @param fieldValues the field-values
     * @param zone the zone
     * @return the partial temporal
     */
    public static PartialTemporal of(Map<TemporalField, Long> fieldValues, ZoneId zone) {
        Objects.requireNonNull(fieldValues, "fieldValues");
        Objects.requireNonNull(zone, "zone");
        TreeMap<TemporalField, Long> map = createMap();
        map.putAll(fieldValues);
        return new PartialTemporal(map, zone);
    }

    /**
     * Obtains an instance from a {@code TemporalAccessor}.
     * <p>
     * This queries the specified temporal object for {@link ChronoField} instances.
     * Any {@code ChronoField} that is supported will have its value set in the resulting partial.
     * <p>
     * The zone is obtained from the temporal using {@link TemporalQueries#zone()}.
     *
     * @param temporal the temporal to copy from
     * @return the partial temporal
     */
    public static PartialTemporal from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        TreeMap<TemporalField, Long> map = createMap();
        Stream.of(ChronoField.values())
                .filter(temporal::isSupported)
                .forEach(field -> map.put(field, temporal.getLong(field)));
        return new PartialTemporal(map, temporal.query(TemporalQueries.zone()));
    }

    private static TreeMap<TemporalField, Long> createMap() {
        return new TreeMap<>(Temporals.fieldComparator().reversed());
    }

    private PartialTemporal(TreeMap<TemporalField, Long> fieldValues, @Nullable ZoneId zone) {
        Objects.requireNonNull(fieldValues, "fieldValues");
        fieldValues.forEach((field, value) -> {
            Objects.requireNonNull(field, "field");
            Objects.requireNonNull(value, "value for field " + field);
        });
        this.fieldValues = fieldValues;
        this.zone = zone;
    }

    /**
     * Returns the number of field-value pairs in this partial temporal.
     *
     * @return the size of the internal field-value map
     */
    public int size() {
        return fieldValues.size();
    }

    /**
     * Gets the chronology, which is always ISO.
     *
     * @return the chronology
     */
    public Chronology getChronology() {
        return IsoChronology.INSTANCE;
    }

    /**
     * Gets the zone, empty if not present.
     *
     * @return the zone
     */
    public Optional<ZoneId> getZone() {
        return Optional.ofNullable(zone);
    }

    /**
     * Gets the map of field-value pairs, immutable.
     *
     * @return the map of field-value pairs
     */
    public NavigableMap<TemporalField, Long> getFields() {
        return Collections.unmodifiableNavigableMap(fieldValues);
    }

    /**
     * Returns a copy of this partial temporal with the specified field set to a new value.
     * <p>
     * This operates with the same semantics as {@code Map.put}.
     *
     * @param field the field to set
     * @param value the value to set
     * @return the new instance with updated field-value map
     */
    public PartialTemporal withField(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        TreeMap<TemporalField, Long> map = createMap();
        map.putAll(fieldValues);
        map.put(field, value);
        return new PartialTemporal(map, zone);
    }

    /**
     * Returns a copy of this partial temporal with the specified field removed.
     * <p>
     * If this partial temporal did not previously support the field, no error occurs.
     *
     * @param field the field type to remove, not null
     * @return a copy of this instance with the field removed
     */
    public PartialTemporal withoutField(TemporalField field) {
        Objects.requireNonNull(field, "field");
        TreeMap<TemporalField, Long> map = createMap();
        map.putAll(fieldValues);
        map.remove(field);
        return new PartialTemporal(map, zone);
    }

    /**
     * Returns a copy of this partial temporal with the specified zone.
     * <p>
     * If the zone is null, the returned partial temporal will have no zone.
     *
     * @param zone the zone to set, may be null for no zone
     * @return a copy of this instance with the zone set
     */
    public PartialTemporal withZone(@Nullable ZoneId zone) {
        return new PartialTemporal(fieldValues, zone);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation simply checks whether the internal map contains the field.
     * No attempt is made to derive fields.
     *
     * @param field the field to check, null returns false
     * @return true if the field is supported, false otherwise
     */
    @Override
    public boolean isSupported(@Nullable TemporalField field) {
        return field != null && fieldValues.containsKey(field);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation simply returns the value from the internal map.
     * No attempt is made to derive fields.
     *
     * @param field the field to check, null returns false
     * @return the value, if present
     * @throws UnsupportedTemporalTypeException if the field is not present
     */
    @Override
    public long getLong(TemporalField field) {
        Long value = fieldValues.get(field);
        if (value == null) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation simply returns the range of the field, if present.
     * No attempt is made to refine the range based on other fields.
     *
     * @param field the field to query the range for
     * @return the range
     */
    @Override
    public ValueRange range(TemporalField field) {
        if (!fieldValues.containsKey(field)) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.range();
    }

    /**
     * Queries this partial temporal.
     * <p>
     * This implementation provides the following support:
     * <ul>
     * <li>{@link TemporalQueries#zoneId()} - returns the zone, if present
     * <li>{@link TemporalQueries#chronology()} - returns {@link IsoChronology#INSTANCE}
     * <li>{@link TemporalQueries#localDate()} - returns a {@code LocalDate} if the {@code EPOCH_DAY} field is present
     * <li>{@link TemporalQueries#localTime()} - returns a {@code LocalTime} if the {@code NANO_OF_DAY} field is present
     * <li>{@link TemporalQueries#offset()} - returns a {@code ZoneOffset} if the {@code OFFSET_SECONDS} field is present, or if the zone is a {@code ZoneOffset}
     * <li>{@link TemporalQueries#zone()} - returns the zone, if present
     * <li>{@link TemporalQueries#precision()} - returns null, as this is not a complete date/time
     * </ul>
     *
     * @param <R> the query result type
     * @param query the query to invoke, not null
     * @return the query result, null if query could not be satisfied
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R extends @Nullable Object> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId()) {
            return (R) zone;
        } else if (query == TemporalQueries.chronology()) {
            return (R) IsoChronology.INSTANCE;
        } else if (query == TemporalQueries.localDate()) {
            return (R) (fieldValues.containsKey(EPOCH_DAY) && EPOCH_DAY.range().isValidValue(fieldValues.get(EPOCH_DAY)) ?
                    LocalDate.ofEpochDay(fieldValues.get(EPOCH_DAY)) :
                    null);
        } else if (query == TemporalQueries.localTime()) {
            return (R) (fieldValues.containsKey(NANO_OF_DAY) && NANO_OF_DAY.range().isValidValue(fieldValues.get(NANO_OF_DAY)) ?
                    LocalTime.ofNanoOfDay(fieldValues.get(NANO_OF_DAY)) :
                    null);
        } else if (query == TemporalQueries.offset()) {
            Long offsetSecs = fieldValues.get(OFFSET_SECONDS);
            if (offsetSecs != null && OFFSET_SECONDS.range().isValidValue(offsetSecs)) {
                return (R) ZoneOffset.ofTotalSeconds(offsetSecs.intValue());
            }
            if (zone instanceof ZoneOffset) {
                return (R) zone;
            }
            return null;
        } else if (query == TemporalQueries.zone()) {
            return query.queryFrom(this);
        } else if (query == TemporalQueries.precision()) {
            return null;  // not a complete date/time
        }
        return TemporalAccessor.super.query(query);
    }

    /**
     * Checks if this partial temporal matches the specified temporal.
     * <p>
     * A match occurs when all the fields of this partial temporal are supported by the specified
     * temporal, and they have the same value.
     * Note that the other temporal may have other additional fields, which are not checked.
     *
     * @param temporal a temporal to check against
     * @return true if this partial temporal matches the specified temporal
     */
    public boolean matches(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        return fieldValues.entrySet().stream()
                .allMatch(entry -> temporal.isSupported(entry.getKey()) && temporal.getLong(entry.getKey()) == entry.getValue());
    }

    /**
     * Resolves this partial temporal into a {@code TemporalAccessor} using the specified resolver style.
     * <p>
     * This method returns a {@code TemporalAccessor} that can be queried for resolved values, such as date and time.
     * It behaves just like the fields were formatted to a string and parsed back using a {@code DateTimeFormatter}
     * with the specified resolver style.
     * An exception will be thrown if the resolution fails, for example due to a conflict between fields.
     * <p>
     * For example, to obtain a {@code LocalDate} from a partial temporal:
     * <pre>{@code
     *   LocalDate date = LocalDate.from(partial.resolve(ResolverStyle.SMART));
     * }</pre>
     *
     * @param resolverStyle the resolver style to use, not null
     * @return the resolved {@code TemporalAccessor}, not null
     * @throws DateTimeException if an error occurs during resolution
     */
    public TemporalAccessor resolve(ResolverStyle resolverStyle) {
        Objects.requireNonNull(resolverStyle, "resolverStyle");
        DateTimeFormatterBuilder formatBuilder = new DateTimeFormatterBuilder();
        fieldValues.keySet().forEach(field -> formatBuilder.appendValue(field).appendLiteral('|'));
        DateTimeFormatter formatter = formatBuilder.toFormatter()
                .withZone(zone)
                .withChronology(IsoChronology.INSTANCE)
                .withResolverStyle(resolverStyle);
        String formatted = format(formatter);
        return formatter.parse(formatted);
    }

    /**
     * Formats this partial temporal using the specified formatter.
     * <p>
     * This partial temporal will be passed to the formatter to produce a string.
     *
     * @param formatter the formatter to use, not null
     * @return the formatted partial temporal string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    /**
     * Checks if this partial temporal is equal to another partial temporal.
     *
     * @param obj the other partial temporal to compare to, null returns false
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof PartialTemporal &&
                fieldValues.equals(((PartialTemporal) obj).fieldValues) &&
                Objects.equals(zone, ((PartialTemporal) obj).zone);
    }

    /**
     * Returns a suitable hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(fieldValues, zone);
    }

    /**
     * Output this partial temporal using a map-like format.
     * <p>
     * The format of this method may vary and is not to be relied on.
     *
     * @return a suitable descriptive string
     */
    @Override
    public String toString() {
        return "Partial[" + fieldValues + (zone == null ? "" : "," + zone) + "]";
    }
}
