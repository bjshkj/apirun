#!/usr/bin/env bash

NAME=testrobot/qhttprunner
TAG=v2.0

TAG_LOCAL=${NAME}:${TAG}

docker build -t ${TAG_LOCAL} .
