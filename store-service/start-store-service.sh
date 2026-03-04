#!/bin/bash

# Load environment variables from parent config-server .env file
set -a
source ../config-server/.env
set +a

# Start store service
./gradlew bootRun
