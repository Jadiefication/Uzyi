#!/bin/zsh

 cargo build
 # shellcheck disable=SC2164
 cd web

 ./gradlew test