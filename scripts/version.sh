#!/bin/bash
set -e

IMAGE_NAME=$1
BRANCH=$2

if [ -z "$IMAGE_NAME" ] || [ -z "$BRANCH" ]; then
    echo "Usage: $0 <docker-image> <branch>"
    exit 1
fi

LATEST_TAG=$(docker pull "${IMAGE_NAME}:latest" 2>/dev/null || true)
if [ -z "$LATEST_TAG" ]; then
    BASE_VERSION="0.0.0"
else
    BASE_VERSION=$(docker image inspect "${IMAGE_NAME}:latest" --format '{{index .Config.Labels "version"}}' 2>/dev/null || echo "$LATEST_TAG")
    BASE_VERSION=${BASE_VERSION%-dev}
fi

IFS='.' read -r MAJOR MINOR PATCH <<< "$BASE_VERSION"

if [ "$BRANCH" == "dev" ]; then
    PATCH=$((PATCH + 1))
    VERSION="${MAJOR}.${MINOR}.${PATCH}-dev"
elif [ "$BRANCH" == "main" ]; then
    PATCH=$((PATCH + 1)) 
    VERSION="${MAJOR}.${MINOR}.${PATCH}"
else
    echo "Unknown branch: $BRANCH"
    exit 1
fi

echo "$VERSION"
