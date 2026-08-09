package org.threeten.extra;

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;

import java.time.DateTimeException;
import java.time.Duration;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

/**
 * Allows an incomplete "partial" date and/or time to be stored.
 * <p>
 * This class can be thought of as a map of date and/or time components.
 * As such, there are no constraints on the value associated with each field.
 * For example, you can set the month to -5,000 if desired.
 * <p>
 * The class operates in the ISO chronology.
 */
public final class PartialTemporal implements TemporalAccessor {

    /**
     * The parsed fields.
     */
    private final Map<TemporalField, Long> fieldValues;
    /**
     * The parsed zone.
     */
    private final @Nullable ZoneId zone;

    /**
     * Obtains an empty instance.
     * <p>
     * This effectively puts no constraints on the date/time, and can be used to represent any date/time.
     *
     * @return the empty partial
     */
    public static PartialTemporal empty() {
        return new PartialTemporal(new HashMap<>(), null);
    }

    /**
     * Obtains an instance from a single field-value, with no time-zone.
     *
     * @param field the field
     * @param value the value
     * @return the partial
     */
    public static PartialTemporal of(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        Map<TemporalField, Long> fieldValues = new HashMap<>();
        fieldValues.put(field, value);
        return new PartialTemporal(fieldValues, null);
    }

    /**
     * Obtains an instance from a map of field-values, with no time-zone.
     *
     * @param fieldValues the field-values
     * @return the partial
     */
    public static PartialTemporal of(Map<TemporalField, Long> fieldValues) {
        return new PartialTemporal(fieldValues, null);
    }

    /**
     * Obtains an instance from a map of field-values and a time-zone.
     *
     * @param fieldValues the field-values
     * @param zone the zone
     * @return the partial
     */
    public static PartialTemporal of(Map<TemporalField, Long> fieldValues, ZoneId zone) {
        Objects.requireNonNull(fieldValues, "fieldValues");
        Objects.requireNonNull(zone, "zone");
        return new PartialTemporal(fieldValues, zone);
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
     * @return the partial
     */
    public static PartialTemporal from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        Map<TemporalField, Long> fieldValues = new HashMap<>();
        Stream.of(ChronoField.values())
                .filter(temporal::isSupported)
                .forEach(field -> fieldValues.put(field, temporal.getLong(field)));
        return new PartialTemporal(fieldValues, temporal.query(TemporalQueries.zone()));
    }

    private PartialTemporal(Map<TemporalField, Long> fieldValues, @Nullable ZoneId zone) {
        Objects.requireNonNull(fieldValues, "fieldValues");
        fieldValues.forEach((field, value) -> {
            Objects.requireNonNull(field, "field");
            Objects.requireNonNull(value, "value for field " + field);
        });
        this.fieldValues = new HashMap<>(fieldValues);
        this.zone = zone;
    }

    /**
     * Returns the number of fields that have values.
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
     * Gets the set of fields, immutable.
     *
     * @return the set of fields
     */
    public Set<TemporalField> getFields() {
        return Collections.unmodifiableSet(fieldValues.keySet());
    }

    /**
     * Returns a copy of this partial with the specified field set to a new value.
     * <p>
     * This operates with the same semantics as {@code Map.put}.
     *
     * @param field the field to set
     * @param value the value to set
     * @return the new instance with updated field-value map
     */
    public PartialTemporal withField(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        Map<TemporalField, Long> newFieldValues = new HashMap<>(fieldValues);
        newFieldValues.put(field, value);
        return new PartialTemporal(newFieldValues, zone);
    }

    /**
     * Returns a copy of this partial with the specified field removed.
     * <p>
     * If this partial did not previously support the field, no error occurs.
     *
     * @param field the field type to remove, may be null
     * @return a copy of this instance with the field removed
     */
    public PartialTemporal withoutField(TemporalField field) {
        Objects.requireNonNull(field, "field");
        Map<TemporalField, Long> newFieldValues = new HashMap<>(fieldValues);
        newFieldValues.remove(field);
        return new PartialTemporal(newFieldValues, zone);
    }

    /**
     * Returns a copy of this partial with the specified zone.
     * <p>
     * If the zone is null, the returned partial will have no zone.
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
        return fieldValues.containsKey(field);
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
     * Queries this partial.
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
            return query.queryFrom(this);
        } else if (query == TemporalQueries.zone()) {
            return query.queryFrom(this);
        } else if (query == TemporalQueries.precision()) {
            return null;  // not a complete date/time
        }
        return TemporalAccessor.super.query(query);
    }

    /**
     * Checks if this partial match the specified temporal.
     * <p>
     * A match occurs when all the fields of this partial are supported by the specified
     * temporal, and they have the same value.
     * Note that the other temporal may have other additional fields, which are not checked.
     *
     * @param temporal a temporal to check against, null means now in default zone
     * @return true if this partial matches the specified temporal
     */
    public boolean matches(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        return fieldValues.entrySet().stream()
                .allMatch(entry -> temporal.isSupported(entry.getKey()) && temporal.getLong(entry.getKey()) == entry.getValue());
    }

    /**
     * Resolves this partial into a {@code TemporalAccessor} using the specified resolver style.
     * <p>
     * This method returns a {@code TemporalAccessor} that can be queried for resolved values, such as date and time.
     * It behaves just like the fields were formatted to a string and parsed back using a {@code DateTimeFormatter}
     * with the specified resolver style.
     * An exception will be thrown if the resolution fails, for example due to a conflict between fields.
     * <p>
     * For example, to obtain a {@code LocalDate} from a partial:
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
     * Formats this partial using the specified formatter.
     * <p>
     * This partial will be passed to the formatter to produce a string.
     *
     * @param formatter the formatter to use, not null
     * @return the formatted partial string, not null
     * @throws DateTimeException if an error occurs during printing
     */
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    /**
     * Checks if this partial is equal to another partial.
     *
     * @param obj the other partial to compare to, null returns false
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
     * Output this partial using a map-like format.
     * <p>
     * The format of this method may vary and is not to be relied on.
     *
     * @return a suitable descriptive string
     */
    @Override
    public String toString() {
        SortedMap<TemporalField, Long> sortedFieldValues = new TreeMap<>(new FieldComparator());
        sortedFieldValues.putAll(fieldValues);
        return "Partial[fieldValues=" + sortedFieldValues + ", " + zone + "]";
    }

    // compares fields by base unit duration, then range unit duration, then name (reverse order)
    static class FieldComparator implements Comparator<TemporalField> {
        @Override
        public int compare(TemporalField field1, TemporalField field2) {
            if (field1.equals(field2)) {
                return 0;
            }
            Duration base1 = field1.getBaseUnit().getDuration();
            Duration base2 = field2.getBaseUnit().getDuration();
            if (!base1.equals(base2)) {
                return base2.compareTo(base1);
            }
            Duration range1 = field1.getRangeUnit().getDuration();
            Duration range2 = field2.getRangeUnit().getDuration();
            if (!range1.equals(range2)) {
                return range2.compareTo(range1);
            }
            return field2.toString().compareTo(field1.toString());
        }
    }
}
