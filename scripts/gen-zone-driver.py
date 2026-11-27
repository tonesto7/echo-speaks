#!/usr/bin/env python3
"""
Generate drivers/echo-speaks-zone-device.groovy from drivers/echo-speaks-device.groovy.

The two Hubitat drivers must stay separate (existing zone devices are bound to the
"Echo Speaks Zone Device" driver, and each driver's command list is fixed in its
definition block), but their code is identical apart from a few lines. Edit only the
device driver, then run:

    python3 scripts/gen-zone-driver.py           # rewrite the zone driver
    python3 scripts/gen-zone-driver.py --check   # exit 1 if the zone driver is out of date (CI)

Every substitution must match exactly once; if the source changes shape the script
fails instead of writing a broken driver.
"""
import difflib
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "drivers" / "echo-speaks-device.groovy"
DST = ROOT / "drivers" / "echo-speaks-zone-device.groovy"

MARKER = ("// GENERATED FILE - do not edit. Source: drivers/echo-speaks-device.groovy | "
          "Regenerate: python3 scripts/gen-zone-driver.py\n")

# (description, old, new) - each `old` must occur exactly once in the source
SUBSTITUTIONS = [
    ("header title",
     "Echo Speaks Device (Hubitat ONLY)",
     "Echo Speaks Zone Device (Hubitat ONLY)"),
    ("isZone()",
     "static Boolean isZone()      { return false }",
     "static Boolean isZone()      { return true }"),
    ("driver name",
     'definition (name: "Echo Speaks Device",',
     'definition (name: "Echo Speaks Zone Device",'),
    ("importUrl",
     'importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/drivers/echo-speaks-device.groovy")',
     'importUrl: "https://raw.githubusercontent.com/tonesto7/echo-speaks/master/drivers/echo-speaks-zone-device.groovy")'),
]


def generate() -> str:
    text = SRC.read_text(encoding="utf-8")
    for desc, old, new in SUBSTITUTIONS:
        count = text.count(old)
        if count != 1:
            sys.exit(f"gen-zone-driver: expected exactly 1 match for {desc!r} in {SRC.name}, found {count}")
        text = text.replace(old, new)
    # marker goes after the first line so the groovylint directive on line 1 keeps working
    first, sep, rest = text.partition("\n")
    return first + sep + MARKER + rest


def main() -> int:
    out = generate()
    current = DST.read_text(encoding="utf-8") if DST.exists() else ""
    if "--check" in sys.argv[1:]:
        if current == out:
            print(f"{DST.relative_to(ROOT)} is up to date")
            return 0
        diff = difflib.unified_diff(current.splitlines(True), out.splitlines(True),
                                    fromfile=f"{DST.name} (committed)", tofile=f"{DST.name} (generated)", n=1)
        sys.stdout.writelines(list(diff)[:200])
        print(f"\n{DST.relative_to(ROOT)} is out of date: edit {SRC.relative_to(ROOT)} and run "
              f"python3 scripts/gen-zone-driver.py")
        return 1
    if current != out:
        DST.write_text(out, encoding="utf-8")
        print(f"wrote {DST.relative_to(ROOT)}")
    else:
        print(f"{DST.relative_to(ROOT)} already up to date")
    return 0


if __name__ == "__main__":
    sys.exit(main())
