#!/usr/bin/env python3
"""Local DAIADS server that does not expose internal development files."""

from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
import posixpath
from urllib.parse import unquote, urlsplit


HOST = "127.0.0.1"
PORT = 8099
WEB_ROOT = Path(__file__).resolve().parents[3]
PROTECTED_PREFIXES = ("/DAIADS/internal", "/DAIADS/.git")
PROTECTED_PATHS = {
    "/DAIADS/.gitignore",
    "/DAIADS/.htaccess",
    "/DAIADS/AGENTS.md",
}


class ProtectedSiteHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(WEB_ROOT), **kwargs)

    def send_head(self):
        request_path = unquote(urlsplit(self.path).path)
        normalized = posixpath.normpath(request_path)
        protected_prefix = any(
            normalized == prefix or normalized.startswith(prefix + "/")
            for prefix in PROTECTED_PREFIXES
        )

        if protected_prefix or normalized in PROTECTED_PATHS:
            self.send_error(404)
            return None

        return super().send_head()


if __name__ == "__main__":
    with ThreadingHTTPServer((HOST, PORT), ProtectedSiteHandler) as server:
        print(f"Serving {WEB_ROOT} at http://{HOST}:{PORT}")
        server.serve_forever()
