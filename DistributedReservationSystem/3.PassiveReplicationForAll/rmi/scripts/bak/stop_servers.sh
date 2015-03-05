#!/bin/bash

. ${SRC512_HOME}/512_env

ssh ${TMGR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/stop_javapgms.sh 
#ssh ${HTL_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/stop_javapgms.sh 
#ssh ${CAR_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/stop_javapgms.sh 
#ssh ${FLT_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/stop_javapgms.sh 
#ssh ${MIDW_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/stop_javapgms.sh 

