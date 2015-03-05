#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

LogFile=${LOG_DIR}/$(basename $0 .sh).$(date '+%Y.%m.%d_%H.%M.%S').log
exec >${LogFile}
exec 2>&1

env

java -cp ${CHARON_CLASSPATH}:${MIDW_SRC}:${SHARED_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar MWImpl.MWServer
