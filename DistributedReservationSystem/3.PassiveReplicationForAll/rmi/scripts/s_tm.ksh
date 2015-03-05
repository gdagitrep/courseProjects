#!/usr/bin/ksh

. ${SRC512_HOME}/512_env
. ${SRC512_HOME}/512_fns.sh

gen_tmgr_replic_cfg

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
hostname
set -x

export TMGR_RMI_REGISTRY_HOST=$1
export TMGR_RMI_PORT=$2
export TMGR_RMI_OBJ_NAME=$3

export PROCESS_ID=${TMGR_RMI_OBJ_NAME}
export LogFile=${LogDir}/${TMGR_RMI_OBJ_NAME}.${HOSTNAME}.${TMGR_RMI_PORT}.$(date '+%Y.%m.%d_%H.%M.%S').log

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:///${TX_SRC}/ -Djava.security.policy=${SRC512_HOME}/tmanager.policy -cp ${TX_SRC}/jango.jar:${TX_SRC}/TxIntf.jar:${TX_SRC}/TxSystem.jar:${TX_SRC}/G13Shared.jar:${TX_SRC}: TxImp.TxnManager  > ${LogFile} 2>&1 &
sleep 5
