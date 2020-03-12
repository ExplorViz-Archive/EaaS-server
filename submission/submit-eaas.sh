#!/bin/sh
set -eu

# Set the following environment variables for using this script:
# EAAS_URL: Base URL where your EaaS instance is reachable without trailing /
# EAAS_PROJECT: ID of the project in EaaS, can be obtained from the Settings page
# EAAS_SECRET: A secret for this project from the Secrets page
# BUILD_NAME: Name that will be displayed in the EaaS interface, must be unique within the EaaS project
# IMAGE_CONTEXT: Context directory for the docker image build. Optional, "." by default
# IMAGE_DOCKERFILE: Path to the Dockerfile within the context. Optional, "Dockerfile" by default

hash "docker" 2>/dev/null || { echo "Command docker missing" >&2; exit 1; }
hash "curl" 2>/dev/null || { echo "Command curl missing" >&2; exit 1; }

echo "Building image" >&2
IMAGE_ID="$(docker build -q -f "${IMAGE_DOCKERFILE:-Dockerfile}" "${IMAGE_CONTEXT:-.}")"
echo "Built image: $IMAGE_ID" >&2

echo "Uploading to EaaS with name: $BUILD_NAME" >&2
BUILD_ID="$(docker image save "$IMAGE_ID" | curl -fsS -X POST -H "X-EaaS-Secret: $EAAS_SECRET" \
      -F "name=$BUILD_NAME" -F "imageID=$IMAGE_ID" -F "image=@-" "$EAAS_URL/api/v1/projects/$EAAS_PROJECT/builds")"
echo "This build has ID #$BUILD_ID" >&2

# Optional; if your EaaS instance runs on the same machine as you CI builds then you MUST comment this out
echo "Removing image locally" >&2
docker image rm "$IMAGE_ID"
