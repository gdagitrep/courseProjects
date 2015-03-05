#!/bin/bash

set -x

. $SRC512_HOME/512_env

rm -f ${CLIENT_SRC}/MWInterface.jar
ln -s ${MIDW_SRC}/MWInterface.jar ${CLIENT_SRC}/MWInterface.jar

${JAVAC} -cp ${CHARON_CLASSPATH}:${CLIENT_SRC}/MWInterface.jar ${CLIENT_SRC}/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

exit 0
