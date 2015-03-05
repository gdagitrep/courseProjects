#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

export SRVR_RMI_OBJ_NAME=${CAR_SRVR_RMI_OBJ_NAME}
export SRVR_RMI_REGISTRY_HOST=${CAR_SRVR_RMI_REGISTRY_HOST}
export SRVR_RMI_PORT=${CAR_SRVR_RMI_PORT}

java -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=${SRC512_HOME}/server.policy -cp ${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar -Djava.rmi.server.codebase=file:///${SERVER_SRC}/ ResImpl.ResourceManagerImpl
