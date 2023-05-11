#!/usr/bin/env bash

NAME=python3
TAG=base

TAG_LOCAL=${NAME}:${TAG}

docker build -t ${TAG_LOCAL} .