#!/bin/bash
docker build -t hawk:latest -f ../Dockerfile --no-cache .
cmd="docker run -e BOT_TOKEN='$1' -v $2:/data hawk:latest"
eval $cmd
