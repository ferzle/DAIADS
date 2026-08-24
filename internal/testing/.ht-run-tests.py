#!/usr/bin/env python3
"""Run the protected Playwright project from a non-web-accessible temp area."""

from hashlib import sha256
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile


TESTING_DIR = Path(__file__).resolve().parent
REPOSITORY_ROOT = TESTING_DIR.parents[1]
RUNTIME_DIR = Path(tempfile.gettempdir()) / "daiads-testing"
PACKAGE_FILE = TESTING_DIR / ".ht-package.json"
LOCK_FILE = TESTING_DIR / ".ht-package-lock.json"


def prepare_dependencies():
    RUNTIME_DIR.mkdir(parents=True, exist_ok=True)
    shutil.copy2(PACKAGE_FILE, RUNTIME_DIR / "package.json")
    shutil.copy2(LOCK_FILE, RUNTIME_DIR / "package-lock.json")

    lock_digest = sha256(LOCK_FILE.read_bytes()).hexdigest()
    digest_file = RUNTIME_DIR / ".package-lock.sha256"
    installed = RUNTIME_DIR / "node_modules" / ".bin" / "playwright"
    recorded_digest = digest_file.read_text().strip() if digest_file.exists() else ""

    if not installed.exists() or (recorded_digest and recorded_digest != lock_digest):
        subprocess.run(["npm", "ci"], cwd=RUNTIME_DIR, check=True)

    digest_file.write_text(lock_digest + "\n")


def main():
    prepare_dependencies()
    environment = os.environ.copy()
    environment["NODE_PATH"] = str(RUNTIME_DIR / "node_modules")
    command = [
        str(RUNTIME_DIR / "node_modules" / ".bin" / "playwright"),
        "test",
        "--config",
        str(TESTING_DIR / ".ht-playwright.config.js"),
        *sys.argv[1:],
    ]
    completed = subprocess.run(command, cwd=REPOSITORY_ROOT, env=environment)
    raise SystemExit(completed.returncode)


if __name__ == "__main__":
    main()
