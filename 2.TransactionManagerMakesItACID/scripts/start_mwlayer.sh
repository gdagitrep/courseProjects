#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
export PROCESS_ID=${MIDW_RMI_OBJ_NAME}
export LogFile=${LogDir}/${MIDW_RMI_OBJ_NAME}.$(date '+%Y.%m.%d_%H.%M.%S').log

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:///${MIDW_SRC}/ -Djava.security.policy=${SRC512_HOME}/middleware.policy -cp ${MIDW_SRC}:${SHARED_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar:${MIDW_SRC}/TxIntf.jar:${MIDW_SRC}/TxSystem.jar MWImpl.MWServer  > ${LogFile}  2>&1 &
sleep 5
