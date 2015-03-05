#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

export SRVR_RMI_OBJ_NAME=${HTL_SRVR_RMI_OBJ_NAME}
export SRVR_RMI_REGISTRY_HOST=${HTL_SRVR_RMI_REGISTRY_HOST}
export SRVR_RMI_PORT=${HTL_SRVR_RMI_PORT}

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
export PROCESS_ID=${SRVR_RMI_OBJ_NAME}
export LogFile=${LogDir}/${SRVR_RMI_OBJ_NAME}.$(date '+%Y.%m.%d_%H.%M.%S').log

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=${SRC512_HOME}/server.policy -cp ${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar:${SERVER_SRC}/TxSystem.jar:${SERVER_SRC}/TxIntf.jar -Djava.rmi.server.codebase=file:///${SERVER_SRC}/ ResImpl.ResourceManagerImpl  > ${LogFile} 2>&1 &
sleep 5
