#!/bin/sh
#shellcheck shell=ash
set -eo pipefail

# Allow our eaas user to access the docker socket on the host
if [ -n "$GROUP_ID_DOCKER" ]; then
    if getent group "$GROUP_ID_DOCKER" >/dev/null; then
        # delete whatever useless group occupies the ID we need
        # laughable but true, busybox can't delete a group by its gid
        delgroup "$(getent group "$GROUP_ID_DOCKER" | cut -d: -f1)"
    fi

    addgroup -g "$GROUP_ID_DOCKER" docker
    addgroup eaas docker
fi

unset GROUP_ID_DOCKER

su-exec eaas "$@"
