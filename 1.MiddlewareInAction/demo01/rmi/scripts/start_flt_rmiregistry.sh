#!/bin/ksh

set -x
. $SRC512_HOME/512_env

rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false ${FLT_SRVR_RMI_PORT}
