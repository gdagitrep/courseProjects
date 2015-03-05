#!/bin/bash

set -x

. $SRC512_HOME/512_env

${JAVAC} -cp ${CHARON_CLASSPATH} ${SERVER_SRC}/ResInterface/ResourceManager.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

java -cp ${CHARON_CLASSPATH}:${SERVER_SRC} charon.Charon ResInterface.ResourceManager ${SERVER_SRC}/ResInterface/
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${JAVAC} -cp ${CHARON_CLASSPATH}:${SERVER_SRC}  ${SERVER_SRC}/ResInterface/ResourceManager_Proxy.java ${SERVER_SRC}/ResInterface/ResourceManager_Skel.java 
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${SERVER_SRC}
jar cvf ResInterface.jar ResInterface/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;
cd -

${JAVAC} -cp ${CHARON_CLASSPATH}:${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar ${SERVER_SRC}/ResImpl/ResourceManagerImpl.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

exit 0
