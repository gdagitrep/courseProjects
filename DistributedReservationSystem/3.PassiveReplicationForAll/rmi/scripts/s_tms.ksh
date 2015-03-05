#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

TMGR_REPLIC_CNT=$(echo ${#TMGR_RMI_REG_HOSTS[@]})
cnt=0
while [[ $cnt -lt $TMGR_REPLIC_CNT ]]
do
  ssh ${TMGR_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/s_tm.ksh ${TMGR_RMI_REG_HOSTS[$cnt]} ${TMGR_RMI_PORTS[$cnt]} ${TMGR_RMI_OBJ_NAMES[$cnt]}

  let "cnt = $cnt + 1"
  sleep 5
done

