#!/bin/bash

. ${SRC512_HOME}/512_env

ssh ${TMGR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_tmanager.sh &
sleep 2
#ssh ${HTL_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_htl_server.sh &
#sleep 2
#ssh ${CAR_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_car_server.sh &
#sleep 2
#ssh ${FLT_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_flt_server.sh &
#sleep 2
#ssh ${MIDW_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/start_mwlayer.sh &

#sleep 10
wait

