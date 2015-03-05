#!/bin/bash

. ${SRC512_HOME}/512_env

ssh ${TMGR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_tm_rmiregistry.sh &
sleep 2
ssh ${HTL_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_htl_rmiregistry.sh &
sleep 2
ssh ${CAR_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_car_rmiregistry.sh &
sleep 2
ssh ${FLT_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_flt_rmiregistry.sh &
sleep 2
ssh ${MIDW_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_mw_rmiregistry.sh &

sleep 10
wait

#for reg in start_tm_rmiregistry.sh start_car_rmiregistry.sh start_flt_rmiregistry.sh start_htl_rmiregistry.sh start_mw_rmiregistry.sh
#do
#  ${SCRIPT_DIR}/${reg} & 
#done

#wait
