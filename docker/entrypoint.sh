#!/bin/sh
#shellcheck shell=ash
set -euo pipefail

DOCKER_SOCKET="/var/run/docker.sock"

# Allow our eaas user to access the docker socket on the host without root
if [ -e "$DOCKER_SOCKET" ]; then
    gid="$(stat -c '%g' "$DOCKER_SOCKET")"
    echo "[entrypoint] Found $DOCKER_SOCKET, owned by group id $gid" >&2

    if getent group "$gid" >/dev/null; then
        gname="$(getent group "$gid" | cut -d: -f1)"
        echo "[entrypoint] Group id belongs to group $gname. Deleting it" >&2
        delgroup "$gname"
    fi

    addgroup -g "$gid" docker
    addgroup eaas docker
fi

exec su-exec eaas "$@"
