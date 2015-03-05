#!/bin/bash

set -x

. $SRC512_HOME/512_env

rm -f ${MIDW_SRC}/ResInterface.jar
ln -s ${SERVER_SRC}/ResInterface.jar ${MIDW_SRC}/ResInterface.jar

${JAVAC} -cp ${CHARON_CLASSPATH} ${MIDW_SRC}/MWInterface/MWLayer.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

java -cp ${CHARON_CLASSPATH}:${MIDW_SRC} charon.Charon MWInterface.MWLayer ${MIDW_SRC}/MWInterface/
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${JAVAC} -cp ${CHARON_CLASSPATH}:${MIDW_SRC} ${MIDW_SRC}/MWInterface/MWLayer_Skel.java  ${MIDW_SRC}/MWInterface/MWLayer_Proxy.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${MIDW_SRC}
jar cvf MWInterface.jar MWInterface/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

${JAVAC} -cp ${CHARON_CLASSPATH}:${MIDW_SRC}:${SHARED_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar ${MIDW_SRC}/MWImpl/MWServer.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;


exit 0
