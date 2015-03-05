#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

java -cp ${CHARON_CLASSPATH}:${CLIENT_SRC}:${CLIENT_SRC}/MWInterface.jar client
