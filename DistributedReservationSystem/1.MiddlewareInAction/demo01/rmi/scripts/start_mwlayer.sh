#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:///${MIDW_SRC}/ -Djava.security.policy=${SRC512_HOME}/middleware.policy -cp ${MIDW_SRC}:${SHARED_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar MWImpl.MWServer
