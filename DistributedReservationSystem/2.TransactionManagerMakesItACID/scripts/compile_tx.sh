#!/bin/bash

set -x

. $SRC512_HOME/512_env

rm ${TX_SRC}/G13Shared.jar
ln -s ${SHARED_SRC}/G13Shared.jar ${TX_SRC}/G13Shared.jar

${JAVAC} ${TX_SRC}/TxSystem/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${TX_SRC}
jar cvf TxSystem.jar TxSystem/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

${JAVAC} -cp ${TX_SRC}/TxSystem.jar ${TX_SRC}/TxIntf/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${TX_SRC}
jar cvf TxIntf.jar TxIntf/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

${JAVAC} -cp ${TX_SRC}/TxSystem.jar:${TX_SRC}/TxIntf.jar:${TX_SRC}/G13Shared.jar ${TX_SRC}/TxImp/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;


echo '
grant codeBase "file:'${TX_SRC}/'" {
    permission java.security.AllPermission;
};
' >  ${SRC512_HOME}/tmanager.policy
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;


exit 0
