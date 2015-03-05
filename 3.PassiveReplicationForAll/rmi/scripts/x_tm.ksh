#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

hostname
ps -fu $LOGNAME | grep TxImp.TxnManager | grep -v -e grep -e awk -e kill -e vi | awk '{ print $2 }' | xargs kill 2>/dev/null
