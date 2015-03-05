#!/usr/bin/ksh

. ${SRC512_HOME}/512_env
TMGR_REPLIC_CNT=$(echo ${#TMGR_RMI_REG_HOSTS[@]})
cnt=0

while [[ $cnt -lt $TMGR_REPLIC_CNT ]]
do
	ssh ${TMGR_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/x_tm.ksh
	let "cnt = $cnt + 1"
done
