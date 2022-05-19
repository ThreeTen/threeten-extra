package org.threeten.extra;

/**
 * A time clock rounding mode.
 * <p>
 * {@code RoundingMode} specifies a time clock rounding strategy.
 * <p>
 * Rounding mode defines the following strategies:
 * <li> DOWN - rounds the time towards the start of a fraction
 * <li> HALF_UP - rounds towards the "nearest neighbor". UP, if the distance is of equal length
 * <li> UP - rounds the time towards the end of a fraction
 */
public enum RoundingMode {
    DOWN,
    HALF_UP,
    UP
}
