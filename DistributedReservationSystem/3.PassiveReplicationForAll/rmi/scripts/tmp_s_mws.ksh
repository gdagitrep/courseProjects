#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

#MIDW_REPLIC_CNT=$(echo ${#MIDW_RMI_REG_HOSTS[@]})
MIDW_REPLIC_CNT=3
cnt=2
while [[ $cnt -lt $MIDW_REPLIC_CNT ]]
do
  ssh ${MIDW_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/s_mw.ksh ${MIDW_RMI_REG_HOSTS[$cnt]} ${MIDW_RMI_PORTS[$cnt]} ${MIDW_RMI_OBJ_NAMES[$cnt]}

  let "cnt = $cnt + 1"
  sleep 5
done

