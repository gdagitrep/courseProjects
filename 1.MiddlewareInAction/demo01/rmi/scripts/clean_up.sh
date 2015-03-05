#!/bin/bash

. ${SRC512_HOME}/512_env

find ${SRC512_HOME}/ -name '*.class'  -o -name '*.jar' -o -name '*.policy' | xargs rm
