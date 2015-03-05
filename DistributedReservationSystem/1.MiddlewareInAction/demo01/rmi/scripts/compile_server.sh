#!/bin/bash

set -x

. $SRC512_HOME/512_env

${JAVAC} ${SERVER_SRC}/ResInterface/ResourceManager.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${SERVER_SRC}
jar cvf ResInterface.jar ResInterface/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;
cd -

${JAVAC} -cp ${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar ${SERVER_SRC}/ResImpl/ResourceManagerImpl.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

echo '
grant codeBase "file:'${SERVER_SRC}'" {
    permission java.security.AllPermission;
};
' >  ${SRC512_HOME}/server.policy
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

exit 0