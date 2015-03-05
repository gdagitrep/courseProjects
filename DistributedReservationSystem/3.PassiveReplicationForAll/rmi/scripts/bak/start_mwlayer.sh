#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
export PROCESS_ID=${MIDW_RMI_OBJ_NAME}
export LogFile=${LogDir}/${MIDW_RMI_OBJ_NAME}.$(date '+%Y.%m.%d_%H.%M.%S').log

hostname
set -x
export MIDW_RMI_REGISTRY_HOST=$1
export MIDW_RMI_PORT=$2
export MIDW_RMI_OBJ_NAME=$3

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:///${MIDW_SRC}/ -Djava.security.policy=${SRC512_HOME}/middleware.policy -cp ${TOOLS}/jango.jar:${MIDW_SRC}:${SHARED_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar:${MIDW_SRC}/TxIntf.jar:${MIDW_SRC}/TxSystem.jar MWImpl.MWServer  > ${LogFile}  2>&1 &
sleep 5
