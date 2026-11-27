#!/usr/bin/env python3
"""
Rebuild bundles/echospeaks.zip (Hubitat bundle) from the current apps and drivers.

    python3 scripts/build-bundle.py           # rewrite the bundle
    python3 scripts/build-bundle.py --check   # exit 1 if the bundle's code differs from the repo (CI)

install.txt / update.txt (bundle manifest with the app OAuth ids) are carried over unchanged
from the existing zip. Run scripts/gen-zone-driver.py first so the zone driver is current.
"""
import io
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
BUNDLE = ROOT / "bundles" / "echospeaks.zip"

# entry name in the bundle -> source file in the repo
ENTRIES = {
    "tonesto7.EchoSpeaksDevice.groovy": "drivers/echo-speaks-device.groovy",
    "tonesto7.EchoSpeaksZoneDevice.groovy": "drivers/echo-speaks-zone-device.groovy",
    "tonesto7.EchoSpeaks.groovy": "apps/echo-speaks.groovy",
    "tonesto7.EchoSpeaksActions.groovy": "apps/echo-speaks-actions.groovy",
    "tonesto7.EchoSpeaksZones.groovy": "apps/echo-speaks-zones.groovy",
}
CARRY_OVER = ["install.txt", "update.txt"]
FIXED_DATE = (2026, 1, 1, 0, 0, 0)  # constant timestamps so rebuilding unchanged code gives an identical zip


def build() -> bytes:
    with zipfile.ZipFile(BUNDLE) as old:
        carried = {n: old.read(n) for n in CARRY_OVER}
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as z:
        for name, src in ENTRIES.items():
            z.writestr(zipfile.ZipInfo(name, FIXED_DATE), (ROOT / src).read_bytes(), zipfile.ZIP_DEFLATED)
        for name, data in carried.items():
            z.writestr(zipfile.ZipInfo(name, FIXED_DATE), data, zipfile.ZIP_DEFLATED)
    return buf.getvalue()


def stale_entries() -> list:
    with zipfile.ZipFile(BUNDLE) as z:
        return [n for n, src in ENTRIES.items() if z.read(n) != (ROOT / src).read_bytes()]


def main() -> int:
    if "--check" in sys.argv[1:]:
        stale = stale_entries()
        if stale:
            print("bundles/echospeaks.zip is out of date: " + ", ".join(stale) +
                  "\nRun: python3 scripts/build-bundle.py")
            return 1
        print("bundles/echospeaks.zip is up to date")
        return 0
    if not stale_entries():
        print("bundles/echospeaks.zip already up to date")
        return 0
    BUNDLE.write_bytes(build())
    print("wrote bundles/echospeaks.zip")
    return 0


if __name__ == "__main__":
    sys.exit(main())
