#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

clear
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "               Transaction Managers"
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo ""
TMGR_REPLIC_CNT=$(echo ${#TMGR_RMI_REG_HOSTS[@]})
cnt=0
while [[ $cnt -lt $TMGR_REPLIC_CNT ]]
do
	echo "----------"
	echo ${TMGR_RMI_REG_HOSTS[$cnt]}  ${TMGR_RMI_PORTS[$cnt]} ${TMGR_RMI_OBJ_NAMES[$cnt]}
	echo "----------"
	ssh ${TMGR_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/p_tm.ksh
	let "cnt = $cnt + 1"
	echo ""
done
echo ""

echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "               Hotel RMs "
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo ""
HTL_REPLIC_CNT=$(echo ${#HTL_RM_RMI_HOSTS[@]})
cnt=0
while [[ $cnt -lt $HTL_REPLIC_CNT ]]
do
	echo "----------"
	echo ${HTL_RM_RMI_HOSTS[$cnt]}  ${HTL_RM_RMI_PORTS[$cnt]} ${HTL_RM_RMI_OBJ_NAMES[$cnt]}
	echo "----------"
	ssh ${HTL_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/p_rm.ksh
	let "cnt = $cnt + 1"
	echo ""
done
echo ""
echo ""

echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "               Car RMs "
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo ""
CAR_REPLIC_CNT=$(echo ${#CAR_RM_RMI_HOSTS[@]})
cnt=0
while [[ $cnt -lt $CAR_REPLIC_CNT ]]
do
	echo "----------"
	echo ${CAR_RM_RMI_HOSTS[$cnt]}  ${CAR_RM_RMI_PORTS[$cnt]} ${CAR_RM_RMI_OBJ_NAMES[$cnt]}
	echo "----------"
	ssh ${CAR_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/p_rm.ksh
	let "cnt = $cnt + 1"
	echo ""
done
echo ""
echo ""


echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "               Flt RMs "
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo ""
FLT_REPLIC_CNT=$(echo ${#FLT_RM_RMI_HOSTS[@]})
cnt=0
while [[ $cnt -lt $FLT_REPLIC_CNT ]]
do
	echo "----------"
	echo ${FLT_RM_RMI_HOSTS[$cnt]}  ${FLT_RM_RMI_PORTS[$cnt]} ${FLT_RM_RMI_OBJ_NAMES[$cnt]}
	echo "----------"
	ssh ${FLT_RM_RMI_HOSTS[$cnt]} ${SCRIPT_DIR}/p_rm.ksh
	let "cnt = $cnt + 1"
echo ""
done
echo ""
echo ""



echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "               Middleware Servers"
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo ""
MIDW_REPLIC_CNT=$(echo ${#MIDW_RMI_REG_HOSTS[@]})
cnt=0
while [[ $cnt -lt $MIDW_REPLIC_CNT ]]
do
	echo "----------"
	echo ${MIDW_RMI_REG_HOSTS[$cnt]}  ${MIDW_RMI_PORTS[$cnt]} ${MIDW_RMI_OBJ_NAMES[$cnt]}
	echo "----------"
	ssh ${MIDW_RMI_REG_HOSTS[$cnt]} ${SCRIPT_DIR}/p_mw.ksh
	let "cnt = $cnt + 1"
	echo ""
done
