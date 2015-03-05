#!/usr/bin/ksh

. ${SRC512_HOME}/512_env
MIDW_REPLIC_CNT=$(echo ${#MIDW_RMI_REG_HOSTS[@]})
cnt=0

while [[ $cnt -lt $MIDW_REPLIC_CNT ]]
do
	ssh ${MIDW_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/x_mw.ksh
	let "cnt = $cnt + 1"
done
