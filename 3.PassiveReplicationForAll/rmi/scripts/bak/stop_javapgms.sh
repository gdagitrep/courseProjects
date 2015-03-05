#!/bin/ksh

ps -fu $LOGNAME | grep -e java -e top | grep -v -e grep |  awk '{ print $2 }' | xargs kill
