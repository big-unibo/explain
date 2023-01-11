#!/bin/bash
set -e
sudo apt-get install libaio1 libaio-dev
docker-compose build --no-cache