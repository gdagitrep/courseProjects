#!/usr/bin/ksh

set -x

. $SRC512_HOME/512_env

rm ${SERVER_SRC}/TxSystem.jar
ln -s ${TX_SRC}/TxSystem.jar ${SERVER_SRC}/TxSystem.jar

rm ${SERVER_SRC}/TxIntf.jar
ln -s ${TX_SRC}/TxIntf.jar ${SERVER_SRC}/TxIntf.jar

rm ${SERVER_SRC}/jango.jar
ln -s ${TOOLS}/jango.jar ${SERVER_SRC}/jango.jar

${JAVAC} -cp ${SERVER_SRC}/jango.jar:${SERVER_SRC}/TxSystem.jar:${SERVER_SRC}/TxIntf.jar:${SHARED_SRC}/G13Shared.jar:.:  ${SERVER_SRC}/ResInterface/ResourceManager.java ${SERVER_SRC}/ResInterface/ResourceManagerRep.java ${SERVER_SRC}/ResInterface/ResponseObject.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#generate the replic stubs.
${JAVA} -cp ${SERVER_SRC}/jango.jar:${SERVER_SRC}/TxSystem.jar:${SERVER_SRC}/TxIntf.jar:.:${SERVER_SRC} jango.Jango ResInterface.ResourceManager ${SERVER_SRC}/ResInterface 0 ResInterface
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#Compile in the replic stubs too..
${JAVAC} -cp ${SERVER_SRC}/jango.jar:${SERVER_SRC}/TxSystem.jar:${SERVER_SRC}/TxIntf.jar:.:${SERVER_SRC} ${SERVER_SRC}/ResInterface/ResourceManager_RepStub.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${SERVER_SRC}
jar cvf ResInterface.jar ResInterface/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;
cd -

${JAVAC} -cp ${SERVER_SRC}/jango.jar:${SERVER_SRC}/TxSystem.jar:${SERVER_SRC}/TxIntf.jar:${SERVER_SRC}:${SHARED_SRC}/G13Shared.jar ${SERVER_SRC}/ResImpl/ResourceManagerImpl.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

echo '
grant codeBase "file:'${SERVER_SRC}/'" {
    permission java.security.AllPermission;
};
' >  ${SRC512_HOME}/server.policy
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

exit 0
