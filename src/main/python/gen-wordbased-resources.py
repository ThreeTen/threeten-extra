#!/usr/bin/env python3
"""Generate the word-based format resource bundles used by AmountFormats.

This is a development tool. It is not built, packaged or executed by Maven, and
threeten-extra has no runtime dependency on CLDR - only the generated
``.properties`` files are committed.

It fetches per-locale data from the CLDR JSON distribution and writes the
bundles that ``AmountFormats.wordBased`` reads:

===========================  ======================  =========================
FormatStyle                  CLDR unitLength         CLDR listPattern
===========================  ======================  =========================
FULL   wordbased_X           long                    standard
LONG   wordbased-long_X      short                   standard
MEDIUM wordbased-medium_X    short                   unit-short
SHORT  wordbased-short_X     narrow                  unit-narrow
===========================  ======================  =========================

The unit words come from ``units/unitLength/unit[duration-*]/unitPattern`` and
the separators from ``listPatterns/listPattern``. ``LONG`` and ``MEDIUM`` share
their words and differ only in whether the final separator is a word ("and") or
a comma.

Plural forms are the awkward part. CLDR gives a pattern per plural category,
whereas AmountFormats can only test a value against the three predicates
declared in ``PredicateFormat.findPredicate``. For each unit this tool tries
every expressible combination of those predicates, keeps the simplest one that
reproduces CLDR exactly for every integer in CHECK_RANGE, and warns when no
combination does - see ``fit_unit``. A warning means the generated bundle is the
closest achievable approximation, not that it is wrong to use; the existing
hand-written bundles carry the same approximations.

Examples::

    # add a new language, all four styles
    ./gen-wordbased-resources.py --lang uk

    # regenerate only the abbreviated styles for German, overwriting
    ./gen-wordbased-resources.py --lang de --style long --style medium --style short --force

    # preview English without writing anything
    ./gen-wordbased-resources.py --lang en --dry-run

Note that regenerating FULL for a language that already has a bundle will
replace hand-curated wording with CLDR wording; --force is required for that.
"""

import argparse
import json
import os
import sys
import tempfile
import urllib.error
import urllib.request

CLDR_DEFAULT_VERSION = "48.2.1"
CLDR_BASE_URL = "https://raw.githubusercontent.com/unicode-org/cldr-json/{version}/cldr-json"

# property key stem -> CLDR unit type, in the key order used by the bundles
UNITS = [
    ("year", "duration-year"),
    ("month", "duration-month"),
    ("week", "duration-week"),
    ("day", "duration-day"),
    ("hour", "duration-hour"),
    ("minute", "duration-minute"),
    ("second", "duration-second"),
    ("millisecond", "duration-millisecond"),
]

# FormatStyle -> (bundle infix, CLDR unitLength, CLDR listPattern type)
STYLES = {
    "full": ("", "long", "standard"),
    "long": ("-long", "short", "standard"),
    "medium": ("-medium", "short", "unit-short"),
    "short": ("-short", "narrow", "unit-narrow"),
}

# The predicates AmountFormats.PredicateFormat understands, as Python.
# Keep in step with the IntPredicate constants in AmountFormats.
PREDICATES = {
    "One":
        lambda v: v == 1 or v == -1,
    "End1Not11":
        lambda v: abs(v) % 10 == 1 and (abs(v) % 100) // 10 != 1,
    "End234NotTeens":
        lambda v: 2 <= abs(v) % 10 <= 4 and (abs(v) % 100) // 10 != 1,
}

# The CLDR plural category each predicate exists to approximate. The trailing
# fallback slot takes "many" where a language uses it for integers, else "other".
PREDICATE_CATEGORY = {
    "One": "one",
    "End1Not11": "one",
    "End234NotTeens": "few",
}

# Predicate combinations AmountFormats can express, simplest first. The empty
# tuple is the single/plural pair written as WordBased.x / WordBased.xs, which
# behaves exactly like the "One" predicate with two texts. Combinations whose
# predicates map to the same category are omitted as they add only a slot.
CANDIDATES = [
    (),
    ("End1Not11",),
    ("End234NotTeens",),
    ("One", "End234NotTeens"),
    ("End1Not11", "End234NotTeens"),
]

# Integers the generated plural handling is checked against.
CHECK_RANGE = range(0, 1001)

# CLDR evaluates plural rules in this order, falling through to "other".
PLURAL_ORDER = ["zero", "one", "two", "few", "many"]

# Escaped even though .properties are read as UTF-8, because these are invisible
# in a diff and are easily lost by an editor that trims whitespace.
INVISIBLE = {
    " ": "\\u00A0",  # NO-BREAK SPACE
    " ": "\\u202F",  # NARROW NO-BREAK SPACE
    " ": "\\u2009",  # THIN SPACE
    "​": "\\u200B",  # ZERO WIDTH SPACE
    "‌": "\\u200C",  # ZERO WIDTH NON-JOINER
    "‍": "\\u200D",  # ZERO WIDTH JOINER
    "‎": "\\u200E",  # LEFT-TO-RIGHT MARK
    "‏": "\\u200F",  # RIGHT-TO-LEFT MARK
    "؜": "\\u061C",  # ARABIC LETTER MARK
    "⁠": "\\u2060",  # WORD JOINER
}


# -----------------------------------------------------------------------------
# CLDR access

class Cldr:
    """Fetches CLDR JSON, caching each file on disk."""

    def __init__(self, version, cache_dir):
        self.version = version
        self.base = CLDR_BASE_URL.format(version=version)
        self.cache_dir = cache_dir
        self.loaded = {}

    def get(self, path):
        if path in self.loaded:
            return self.loaded[path]
        self.loaded[path] = data = self._load(path)
        return data

    def _load(self, path):
        cached = os.path.join(self.cache_dir, self.version, path.replace("/", "_"))
        if os.path.exists(cached):
            with open(cached, encoding="utf-8") as f:
                return json.load(f)
        url = self.base + "/" + path
        try:
            with urllib.request.urlopen(url) as response:
                raw = response.read().decode("utf-8")
        except urllib.error.HTTPError as ex:
            if ex.code == 404:
                raise Failure("CLDR %s has no %s" % (self.version, path))
            raise Failure("failed to fetch %s: %s" % (url, ex))
        except urllib.error.URLError as ex:
            raise Failure("failed to fetch %s: %s" % (url, ex.reason))
        os.makedirs(os.path.dirname(cached), exist_ok=True)
        with open(cached, "w", encoding="utf-8") as f:
            f.write(raw)
        return json.loads(raw)

    def units(self, tag):
        data = self.get("cldr-units-full/main/%s/units.json" % tag)
        return data["main"][tag]["units"]

    def list_patterns(self, tag):
        data = self.get("cldr-misc-full/main/%s/listPatterns.json" % tag)
        return data["main"][tag]["listPatterns"]

    def plural_rules(self, tag):
        data = self.get("cldr-core/supplemental/plurals.json")
        rules = data["supplemental"]["plurals-type-cardinal"]
        for key in (tag, tag.replace("-", "_"), tag.split("-")[0]):
            if key in rules:
                return {k[len("pluralRule-count-"):]: v for k, v in rules[key].items()}
        raise Failure("CLDR has no plural rules for %s" % tag)


class Failure(Exception):
    """A problem worth reporting to the user rather than a stack trace."""


# -----------------------------------------------------------------------------
# CLDR plural rules
#
# Grammar (UTS #35): condition = and_condition ('or' and_condition)*
#                    and_condition = relation ('and' relation)*
#                    relation = operand ('%' int)? ('=' | '!=') range_list
# Only integers are formatted, so the operands are fixed at v=w=f=t=c=e=0 and
# n=i=abs(value).

def _match_relation(text, operands):
    negated = "!=" in text
    left, right = text.split("!=" if negated else "=", 1)
    left = left.strip()
    if "%" in left:
        name, modulus = left.split("%", 1)
        value = operands[name.strip()] % int(modulus.strip())
    else:
        value = operands[left]
    found = False
    for part in right.split(","):
        part = part.strip()
        if ".." in part:
            low, high = part.split("..", 1)
            found = int(low) <= value <= int(high)
        else:
            found = value == int(part)
        if found:
            break
    return not found if negated else found


def _match_condition(rule, operands):
    condition = rule.split("@")[0].strip()
    if not condition:
        return True
    for alternative in condition.split(" or "):
        if all(_match_relation(rel, operands) for rel in alternative.split(" and ")):
            return True
    return False


def plural_category(rules, value):
    """The CLDR plural category of an integer value."""
    magnitude = abs(value)
    operands = {"n": magnitude, "i": magnitude, "v": 0, "w": 0, "f": 0, "t": 0, "c": 0, "e": 0}
    for name in PLURAL_ORDER:
        rule = rules.get(name)
        if rule is not None and _match_condition(rule, operands):
            return name
    return "other"


# -----------------------------------------------------------------------------
# Fitting CLDR plural categories onto the AmountFormats predicates

def slot_of(candidate, value):
    """The index of the first matching predicate, or the trailing fallback slot."""
    for index, name in enumerate(candidate):
        if PREDICATES[name](value):
            return index
    return len(candidate)


def slot_categories(candidate, used):
    """The CLDR plural category each slot of a predicate combination stands for.

    Taken from what the predicates are for rather than inferred from the
    integers they happen to capture. Inferring it statistically looks appealing
    but gets Czech backwards: "End234NotTeens" captures 2-4 along with 22-24,
    32-34 and so on, and since CLDR Czech puts only 2-4 in "few" the larger part
    of that slot is "other" - so a best-fit rule labels the slot "other" and
    yields "2 let" instead of "2 roky". Fewer integers are then wrong overall,
    but the ones that are wrong are the small values that dominate real output.
    """
    fallback = "many" if "many" in used else "other"
    return [PREDICATE_CATEGORY[name] for name in candidate] + [fallback]


def pattern_for(patterns, category):
    """The unit pattern for a plural category, falling back the way CLDR does."""
    for key in (category, "other", "one"):
        if key in patterns:
            return patterns[key]
    raise Failure("unit has no usable plural patterns: %r" % patterns)


def fit_unit(patterns, categories, used):
    """Pick the simplest predicate combination that reproduces the CLDR patterns.

    Returns (candidate, texts, mismatches) where mismatches lists the integers
    the combination cannot represent. An empty candidate means the plain
    single/plural pair.

    When nothing fits exactly the winner is the combination that distinguishes
    the most plural categories the language actually uses, not the one that is
    wrong least often - keeping a category that is mostly right beats collapsing
    it into a neighbour that is right for more integers overall.
    """
    best = None
    for candidate in CANDIDATES:
        effective = candidate or ("One",)
        slots = slot_categories(effective, used)
        texts = [pattern_for(patterns, c) for c in slots]
        mismatches = [v for v, c in categories.items()
                      if texts[slot_of(effective, v)] != pattern_for(patterns, c)]
        if not mismatches:
            return candidate, texts, []
        rank = (-len({c for c in slots if c in used}), len(mismatches), len(slots))
        if best is None or rank < best[0]:
            best = (rank, candidate, texts, mismatches)
    return best[1], best[2], best[3]


# -----------------------------------------------------------------------------
# Writing .properties

def escape(value):
    """Escape a .properties value, making leading and trailing spaces explicit."""
    out = []
    last = len(value) - 1
    for index, char in enumerate(value):
        if char in INVISIBLE:
            out.append(INVISIBLE[char])
        elif char == "\\":
            out.append("\\\\")
        elif char == "\t":
            out.append("\\t")
        elif char == " ":
            if index == last:
                out.append("\\u0020")
            elif index == 0:
                out.append("\\ ")
            else:
                out.append(" ")
        else:
            out.append(char)
    return "".join(out)


def separator(pattern, description):
    """The literal text between {0} and {1} of a list pattern."""
    if not (pattern.startswith("{0}") and pattern.endswith("{1}")):
        raise Failure(
            "%s list pattern %r is not of the form {0}<separator>{1}, which AmountFormats "
            "cannot represent as it only joins units with a fixed separator"
            % (description, pattern))
    return pattern[3:-3]


def unit_suffix(pattern, description):
    """The literal text following {0} of a unit pattern."""
    if not pattern.startswith("{0}"):
        raise Failure(
            "%s unit pattern %r does not start with {0}, which AmountFormats cannot "
            "represent as it always writes the value before the unit word"
            % (description, pattern))
    return pattern[3:]


def render(tag, style, cldr, warn):
    """Build the text of one bundle."""
    infix, length, list_type = STYLES[style]
    units = cldr.units(tag)
    if length not in units:
        raise Failure("CLDR %s has no %s units" % (tag, length))
    patterns = cldr.list_patterns(tag).get("listPattern-type-" + list_type)
    if patterns is None:
        raise Failure("CLDR %s has no %s list pattern" % (tag, list_type))
    rules = cldr.plural_rules(tag)
    categories = {value: plural_category(rules, value) for value in CHECK_RANGE}
    used = set(categories.values())

    lines = [
        "# Word-based format words for FormatStyle.%s." % style.upper(),
        "# Generated by src/main/python/gen-wordbased-resources.py from CLDR %s:" % cldr.version,
        "#   units/unitLength[@type=\"%s\"]/unit[@type=\"duration-*\"]" % length,
        "#   listPatterns/listPattern[@type=\"%s\"]" % list_type,
        "WordBased.commaspace=%s" % escape(separator(patterns["middle"], list_type)),
        "WordBased.spaceandspace=%s" % escape(separator(patterns["2"], list_type)),
    ]

    for stem, unit_type in UNITS:
        entry = units[length].get(unit_type)
        if entry is None:
            raise Failure("CLDR %s has no %s %s" % (tag, length, unit_type))
        described = "%s %s %s" % (tag, length, unit_type)
        by_category = {key[len("unitPattern-count-"):]: unit_suffix(value, described)
                       for key, value in entry.items()
                       if key.startswith("unitPattern-count-")}
        candidate, texts, mismatches = fit_unit(by_category, categories, used)
        if mismatches:
            warn("%s/%s %s: no exact plural fit, %d of %d integers differ (e.g. %s)"
                 % (tag, style, stem, len(mismatches), len(categories),
                    ", ".join(str(v) for v in mismatches[:5])))
        if not candidate:
            lines.append("WordBased.%s=%s" % (stem, escape(texts[0])))
            lines.append("WordBased.%ss=%s" % (stem, escape(texts[1])))
        else:
            lines.append("WordBased.%ss.predicates=%s" % (stem, "|||".join(candidate)))
            lines.append("WordBased.%ss.list=%s" % (stem, escape("|||".join(texts))))

    return "\n".join(lines) + "\n"


# -----------------------------------------------------------------------------
# CLI

def default_output_dir():
    root = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    return os.path.join(root, "main", "resources", "org", "threeten", "extra")


def parse_args(argv):
    parser = argparse.ArgumentParser(
        description="Generate AmountFormats word-based resource bundles from CLDR.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="Existing files are left alone unless --force is given.")
    parser.add_argument("--lang", action="append", required=True, metavar="TAG",
                        help="language to generate, e.g. 'de' or 'pt-BR'; repeatable")
    parser.add_argument("--style", action="append", choices=sorted(STYLES), metavar="STYLE",
                        help="style to generate (%s); repeatable, default all"
                             % ", ".join(sorted(STYLES)))
    parser.add_argument("--base", action="store_true",
                        help="also write the unsuffixed base bundle from this language, "
                             "leaving the language bundle empty (as English does)")
    parser.add_argument("--output-dir", default=default_output_dir(), metavar="DIR",
                        help="where to write the bundles (default: the project resources)")
    parser.add_argument("--force", action="store_true", help="overwrite existing bundles")
    parser.add_argument("--dry-run", action="store_true",
                        help="print the bundles instead of writing them")
    parser.add_argument("--cldr-version", default=CLDR_DEFAULT_VERSION, metavar="VERSION",
                        help="cldr-json release tag (default: %s)" % CLDR_DEFAULT_VERSION)
    parser.add_argument("--cache-dir", metavar="DIR",
                        default=os.path.join(tempfile.gettempdir(), "cldr-json-cache"),
                        help="where fetched CLDR JSON is cached")
    parser.add_argument("--quiet", action="store_true", help="suppress plural-fit warnings")
    return parser.parse_args(argv)


def main(argv):
    args = parse_args(argv)
    styles = args.style or list(STYLES)
    cldr = Cldr(args.cldr_version, args.cache_dir)
    warnings = []

    def warn(message):
        warnings.append(message)
        if not args.quiet:
            print("warning: " + message, file=sys.stderr)

    written, skipped = [], []
    for lang in args.lang:
        tag = lang.replace("_", "-")
        suffix = lang.replace("-", "_")
        for style in styles:
            infix = STYLES[style][0]
            text = render(tag, style, cldr, warn)
            targets = [("wordbased%s_%s.properties" % (infix, suffix), text)]
            if args.base:
                # the language bundle is left empty so the locale resolves to the base
                targets = [("wordbased%s.properties" % infix, text),
                           ("wordbased%s_%s.properties" % (infix, suffix), "")]
            for name, content in targets:
                path = os.path.join(args.output_dir, name)
                if args.dry_run:
                    print("----- %s -----" % path)
                    print(content, end="")
                    continue
                if os.path.exists(path) and not args.force:
                    skipped.append(name)
                    continue
                os.makedirs(args.output_dir, exist_ok=True)
                with open(path, "w", encoding="utf-8", newline="\n") as f:
                    f.write(content)
                written.append(name)

    if not args.dry_run:
        print("wrote %d bundle(s) to %s" % (len(written), args.output_dir))
        if skipped:
            print("skipped %d existing bundle(s), use --force to overwrite: %s"
                  % (len(skipped), ", ".join(sorted(skipped))))
    if warnings:
        print("%d plural approximation(s); the bundles are the closest achievable fit"
              % len(warnings), file=sys.stderr)
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main(sys.argv[1:]))
    except Failure as failure:
        print("error: %s" % failure, file=sys.stderr)
        sys.exit(1)
    except KeyboardInterrupt:
        sys.exit(130)
