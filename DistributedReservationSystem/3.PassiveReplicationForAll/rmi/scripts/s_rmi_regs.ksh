#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

cnt=0
tmRepCnt=$(echo ${#TMGR_RMI_REG_HOSTS[@]})

while [[ $cnt -lt $tmRepCnt ]]
do
  ssh ${TMGR_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/s_rmireg.ksh ${TMGR_RMI_PORTS[$cnt]} &
	sleep 1
	let "cnt = $cnt + 1"
done

cnt=0
mwRepCnt=$(echo ${#MIDW_RMI_REG_HOSTS[@]})

while [[ $cnt -lt $mwRepCnt ]]
do
  ssh ${MIDW_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/s_rmireg.ksh ${MIDW_RMI_PORTS[$cnt]} &
	sleep 1
	let "cnt = $cnt + 1"
done

cnt=0
rmRepCnt=$(echo ${#HTL_RM_RMI_HOSTS[@]})

while [[ $cnt -lt $rmRepCnt ]]
do
  ssh ${HTL_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/s_rmireg.ksh ${HTL_RM_RMI_PORTS[$cnt]} &
	sleep 1
	let "cnt = $cnt + 1"
done

cnt=0
rmRepCnt=$(echo ${#CAR_RM_RMI_HOSTS[@]})

while [[ $cnt -lt $rmRepCnt ]]
do
  ssh ${CAR_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/s_rmireg.ksh ${CAR_RM_RMI_PORTS[$cnt]} &
	sleep 1
	let "cnt = $cnt + 1"
done

cnt=0
rmRepCnt=$(echo ${#FLT_RM_RMI_HOSTS[@]})

while [[ $cnt -lt $rmRepCnt ]]
do
  ssh ${FLT_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/s_rmireg.ksh ${FLT_RM_RMI_PORTS[$cnt]} &
	sleep 1
	let "cnt = $cnt + 1"
done
