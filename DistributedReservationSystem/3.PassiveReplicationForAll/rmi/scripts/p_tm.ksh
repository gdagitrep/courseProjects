#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

ps -fu $LOGNAME | grep TxImp.TxnManager | grep -v -e grep -e awk -e kill -e vi 
