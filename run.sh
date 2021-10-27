#!/usr/bin/env bash

CONVERSION_MODE="$1"
HOST_INPUT_DIR="$2"
INPUT_FILENAME="$3"

if [ "$CONVERSION_MODE" != "tab" ] && [ "$CONVERSION_MODE" != "csv" ]; then
  echo "The first argument must be either \"tab\" or \"csv\""
  exit
fi

if [ -z "$INPUT_FILENAME" ]; then
  echo "Usage: sh ./run.sh [csv|tab] [host dir containing data files] [csv or json file name]"
  exit
fi

DOCKER_INPUT_DIR="/avarni/reports"
DOCKER_VOLUME="${HOST_INPUT_DIR}:${DOCKER_INPUT_DIR}"
DOCKER_INPUT_FILE="${DOCKER_INPUT_DIR}/${INPUT_FILENAME}"

echo "This will run the Docker image for the JSON transformer app as a container"
echo "Using the ${CONVERSION_MODE} conversion mode"

echo "Running the container with the ${CONVERSION_MODE} app..."
docker run \
  -v "${DOCKER_VOLUME}" \
  avarni-transformer:1.0 \
  $CONVERSION_MODE "${DOCKER_INPUT_FILE}"
