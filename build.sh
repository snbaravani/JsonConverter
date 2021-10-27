#!/usr/bin/env bash

echo "Building the docker image..."
docker build . -t avarni-transformer:1.0
