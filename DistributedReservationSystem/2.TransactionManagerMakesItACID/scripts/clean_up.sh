#!/bin/bash

if [[ -z "${SRC512_HOME}" ]]
then
  echo "Err: SRC512_HOME environment variable is not set, aborting ..." 1>&2
  exit 1
fi


. ${SRC512_HOME}/512_env

find ${SRC512_HOME}/ -name '*.class'  -o -name '*.jar' -o -name '*.policy' | xargs rm

#find ${LOG_DIR} -type f -name '*.log' | xargs rm 2>/dev/null
