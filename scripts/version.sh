#!/bin/bash
set -euo pipefail

IMAGE_NAME="$1"
BRANCH="$2"

if [[ -z "${IMAGE_NAME:-}" || -z "${BRANCH:-}" ]]; then
  echo "Usage: $0 <docker-image> <branch>"
  exit 1
fi

# Split org/repo
ORG=$(echo "$IMAGE_NAME" | cut -d/ -f1)
REPO=$(echo "$IMAGE_NAME" | cut -d/ -f2)

# Fetch tags from Docker Hub (exclude latest & dev)
TAGS=$(curl -s "https://hub.docker.com/v2/repositories/${ORG}/${REPO}/tags/?page_size=100" \
  | jq -r '.results[].name' \
  | grep -E '^[0-9]+\.[0-9]+\.[0-9]+(-[a-z0-9]+)?$' || true)

if [[ -z "$TAGS" ]]; then
  BASE_VERSION="0.0.0"
else
  BASE_VERSION=$(echo "$TAGS" | sort -V | tail -n 1)
fi

# Split numeric part and suffix
VERSION_NUMBER=$(echo "$BASE_VERSION" | cut -d'-' -f1)
IFS='.' read -r MAJOR MINOR PATCH <<< "$VERSION_NUMBER"
PATCH=$((PATCH + 1))

if [[ "$BRANCH" == "dev" ]]; then
  VERSION="${MAJOR}.${MINOR}.${PATCH}-dev"
elif [[ "$BRANCH" == "main" ]]; then
  VERSION="${MAJOR}.${MINOR}.${PATCH}"
else
  echo "Unsupported branch: $BRANCH"
  exit 1
fi

echo "$VERSION"
