#!/usr/bin/ksh

set -x

. $SRC512_HOME/512_env

rm ${MIDW_SRC}/G13Shared.jar
ln -s ${SHARED_SRC}/G13Shared.jar ${MIDW_SRC}/G13Shared.jar

rm ${MIDW_SRC}/TxSystem.jar
ln -s ${TX_SRC}/TxSystem.jar ${MIDW_SRC}/TxSystem.jar

rm ${MIDW_SRC}/jango.jar
ln -s ${TOOLS}/jango.jar ${MIDW_SRC}/jango.jar

rm ${MIDW_SRC}/TxIntf.jar
ln -s ${TX_SRC}/TxIntf.jar ${MIDW_SRC}/TxIntf.jar

rm -f ${MIDW_SRC}/ResInterface.jar
ln -s ${SERVER_SRC}/ResInterface.jar ${MIDW_SRC}/ResInterface.jar

# compile the interface
${JAVAC} -cp ${MIDW_SRC}/TxSystem.jar:${MIDW_SRC}/TxIntf.jar:.: ${MIDW_SRC}/MWInterface/MWLayer.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#generate the replic stubs.
${JAVA} -cp ${MIDW_SRC}/jango.jar:${MIDW_SRC}/TxSystem.jar:${MIDW_SRC}/TxIntf.jar:.:${MIDW_SRC} jango.Jango MWInterface.MWLayer ${MIDW_SRC}/MWInterface 0 MWInterface
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#Compile in the replic stubs too..
${JAVAC} -cp ${MIDW_SRC}/jango.jar:${MIDW_SRC}/TxSystem.jar:${MIDW_SRC}/TxIntf.jar:.:${MIDW_SRC} ${MIDW_SRC}/MWInterface/MWLayer_RepStub.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#create the jar files.
cd  ${MIDW_SRC}
jar cvf MWInterface.jar MWInterface/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

${JAVAC} -cp ${MIDW_SRC}/jango.jar:${MIDW_SRC}/TxSystem.jar:${MIDW_SRC}/TxIntf.jar:${MIDW_SRC}:${MIDW_SRC}/G13Shared.jar:${MIDW_SRC}/ResInterface.jar:.: ${MIDW_SRC}/MWImpl/MWServer.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

echo '
grant codeBase "file:'${MIDW_SRC}/'" {
    permission java.security.AllPermission;
};
' >  ${SRC512_HOME}/middleware.policy
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

exit 0
