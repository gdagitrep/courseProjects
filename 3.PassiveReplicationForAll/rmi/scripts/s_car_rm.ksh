#!/usr/bin/ksh

. ${SRC512_HOME}/512_env
. ${SRC512_HOME}/512_fns.sh

gen_car_replic_cfg
gen_tmgr_replic_cfg

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
hostname
set -x

export RM_TYPE="CAR"
export RM_REPLIC_CFG=${CAR_REPLIC_CFG}
export RM_REPLIC_CNT=${CAR_REPLIC_CNT}

export SRVR_RMI_REGISTRY_HOST=$1
export SRVR_RMI_PORT=$2
export SRVR_RMI_OBJ_NAME=$3

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
export PROCESS_ID=${SRVR_RMI_OBJ_NAME}
export LogFile=${LogDir}/${SRVR_RMI_OBJ_NAME}.${HOSTNAME}.${SRVR_RMI_PORT}.$(date '+%Y.%m.%d_%H.%M.%S').log

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=${SRC512_HOME}/server.policy -cp ${SERVER_SRC}/jango.jar:${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar:${SERVER_SRC}/TxSystem.jar:${SERVER_SRC}/TxIntf.jar -Djava.rmi.server.codebase=file:///${SERVER_SRC}/ ResImpl.ResourceManagerImpl  > ${LogFile} 2>&1 &
sleep 5

