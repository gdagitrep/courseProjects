#!/usr/bin/ksh

. ${SRC512_HOME}/512_env
. ${SRC512_HOME}/512_fns.sh

gen_mw_replic_cfg
gen_tmgr_replic_cfg
gen_htl_replic_cfg
gen_car_replic_cfg
gen_flt_replic_cfg

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
hostname
set -x

export MIDW_RMI_REGISTRY_HOST=$1
export MIDW_RMI_PORT=$2
export MIDW_RMI_OBJ_NAME=$3
export PROCESS_ID=${MIDW_RMI_OBJ_NAME}
export LogFile=${LogDir}/${MIDW_RMI_OBJ_NAME}.${HOSTNAME}.${MIDW_RMI_PORT}.$(date '+%Y.%m.%d_%H.%M.%S').log

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:///${MIDW_SRC}/ -Djava.security.policy=${SRC512_HOME}/middleware.policy -cp ${MIDW_SRC}/jango.jar:${MIDW_SRC}:${MIDW_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar:${MIDW_SRC}/TxIntf.jar:${MIDW_SRC}/TxSystem.jar MWImpl.MWServer  > ${LogFile}  2>&1 &
sleep 5
