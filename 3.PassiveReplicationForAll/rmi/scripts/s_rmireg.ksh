#!/usr/bin/ksh

rport=$1

. ${SRC512_HOME}/512_env

hostname
set -x
nohup rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false ${rport} &

sleep 5
