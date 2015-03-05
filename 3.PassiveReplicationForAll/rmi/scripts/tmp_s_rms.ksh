#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

#HTL_REPLIC_CNT=$(echo ${#HTL_RM_RMI_HOSTS[@]})
#cnt=0
#while [[ $cnt -lt $HTL_REPLIC_CNT ]]
#do
#  ssh ${HTL_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/s_htl_rm.ksh ${HTL_RM_RMI_HOSTS[$cnt]} ${HTL_RM_RMI_PORTS[$cnt]} ${HTL_RM_RMI_OBJ_NAMES[$cnt]}
#
#  let "cnt = $cnt + 1"
#done
#
#
#CAR_REPLIC_CNT=$(echo ${#CAR_RM_RMI_HOSTS[@]})
CAR_REPLIC_CNT=1
cnt=0
while [[ $cnt -lt $CAR_REPLIC_CNT ]]
do
  ssh ${CAR_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/s_car_rm.ksh ${CAR_RM_RMI_HOSTS[$cnt]} ${CAR_RM_RMI_PORTS[$cnt]} ${CAR_RM_RMI_OBJ_NAMES[$cnt]}

  let "cnt = $cnt + 1"
done

#
#FLT_REPLIC_CNT=$(echo ${#FLT_RM_RMI_HOSTS[@]})
#cnt=0
#while [[ $cnt -lt $FLT_REPLIC_CNT ]]
#do
#  ssh ${FLT_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/s_flt_rm.ksh ${FLT_RM_RMI_HOSTS[$cnt]} ${FLT_RM_RMI_PORTS[$cnt]} ${FLT_RM_RMI_OBJ_NAMES[$cnt]}
#
#  let "cnt = $cnt + 1"
#done
#
#
