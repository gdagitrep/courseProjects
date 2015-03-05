#!/bin/bash

ps -fu $LOGNAME|grep java| awk '{ print $2 }' | xargs kill
