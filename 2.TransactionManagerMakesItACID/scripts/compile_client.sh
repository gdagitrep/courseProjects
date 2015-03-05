#!/bin/bash

set -x

. $SRC512_HOME/512_env

rm ${CLIENT_SRC}/G13Shared.jar
ln -s ${SHARED_SRC}/G13Shared.jar ${CLIENT_SRC}/G13Shared.jar

rm -f ${CLIENT_SRC}/MWInterface.jar
ln -s ${MIDW_SRC}/MWInterface.jar ${CLIENT_SRC}/MWInterface.jar

rm ${CLIENT_SRC}/TxSystem.jar
ln -s ${TX_SRC}/TxSystem.jar ${CLIENT_SRC}/TxSystem.jar

${JAVAC} -cp ${CLIENT_SRC}/G13Shared.jar:${TX_SRC}/TxSystem.jar:${CLIENT_SRC}/MWInterface.jar ${CLIENT_SRC}/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

echo '
grant codeBase "file:'${CLIENT_SRC}/'" {
    permission java.security.AllPermission;
};
' >  ${SRC512_HOME}/client.policy
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

exit 0
