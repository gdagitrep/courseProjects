#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

LogFile=${LOG_DIR}/$(basename $0 .sh).$(date '+%Y.%m.%d_%H.%M.%S').log
exec >${LogFile}
exec 2>&1

env

export SRVR_RO_OBJ_NAME=${CAR_SRVR_RO_OBJ_NAME}
export SRVR_RO_PORT=${CAR_SRVR_RO_PORT}

java -cp ${CHARON_CLASSPATH}:${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar ResImpl.ResourceManagerImpl
