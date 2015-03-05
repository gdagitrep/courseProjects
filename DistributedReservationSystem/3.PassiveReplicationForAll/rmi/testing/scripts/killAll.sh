#!/bin/bash

ps -fu $LOGNAME|awk '{ print $2 }' | xargs kill
