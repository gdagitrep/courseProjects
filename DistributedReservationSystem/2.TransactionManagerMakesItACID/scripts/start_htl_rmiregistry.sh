#!/bin/ksh

set -x
. $SRC512_HOME/512_env

nohup rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false ${HTL_SRVR_RMI_PORT} &
sleep 5
