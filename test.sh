#!/bin/zsh

 cargo build
 # shellcheck disable=SC2164
 cd dsl

 ./gradlew test