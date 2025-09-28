#!/bin/sh
curl -f http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1
