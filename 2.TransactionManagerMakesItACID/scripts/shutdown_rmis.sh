#!/bin/ksh

. ${SRC512_HOME}/512_env

ssh ${MIDW_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/shutdown_rmi_regs.sh
ssh ${FLT_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/shutdown_rmi_regs.sh
ssh ${CAR_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/shutdown_rmi_regs.sh
ssh ${HTL_SRVR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/shutdown_rmi_regs.sh
ssh ${TMGR_RMI_REGISTRY_HOST} ${SCRIPT_DIR}/shutdown_rmi_regs.sh
