#!/usr/bin/ksh

hostname
#set -x
ps -fu $LOGNAME | grep rmiregistry | grep -v -e grep -e awk -e ps -e xargs -e kill -e vi |  awk '{ print $2 }' | xargs kill 2>/dev/null
