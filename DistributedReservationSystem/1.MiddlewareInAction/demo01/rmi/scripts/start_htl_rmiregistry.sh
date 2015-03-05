#!/bin/ksh

set -x
. $SRC512_HOME/512_env

rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false ${HTL_SRVR_RMI_PORT}
