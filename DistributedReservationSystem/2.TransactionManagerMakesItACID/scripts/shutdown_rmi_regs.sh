#!/bin/ksh

ps -fu $LOGNAME | grep rmiregistry | awk '{ print $2 }' | xargs kill
