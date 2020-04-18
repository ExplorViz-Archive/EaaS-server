#!/bin/sh
set -eu

APP_USER="eaas"
DOCKER_SOCKET="/var/run/docker.sock"

# Allow our eaas user to access the docker socket on the host without root
if [ -e "$DOCKER_SOCKET" ]; then
    gid="$(stat -c '%g' "$DOCKER_SOCKET")"
    echo "[entrypoint] Found $DOCKER_SOCKET, owned by group id $gid" >&2

    if getent group "$gid" >/dev/null; then
        gname="$(getent group "$gid" | cut -d: -f1)"
        echo "[entrypoint] Deleting group $gname currently using this id" >&2
        delgroup "$gname"
    fi

    addgroup -g "$gid" docker
    addgroup "$APP_USER" docker
fi

# TODO: Consider using -Xshareclasses:readonly, and generating classes AOT by starting application in docker build
exec su-exec "$APP_USER" java -noverify -Xtune:virtualized -Dvaadin.productionMode -cp ".:lib/*" "net.explorviz.eaas.Application" "$@"
