#!//usr/bin/ksh

set -x

. $SRC512_HOME/512_env

rm ${TX_SRC}/G13Shared.jar
ln -s ${SHARED_SRC}/G13Shared.jar ${TX_SRC}/G13Shared.jar

rm ${TX_SRC}/jango.jar
ln -s ${TOOLS}/jango.jar ${TX_SRC}/jango.jar

${JAVAC} ${TX_SRC}/TxSystem/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${TX_SRC}
jar cvf TxSystem.jar TxSystem/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

#remove previous stubs
rm -f ${TX_SRC}/TxIntf/TxnManagerIntf_RepStub.java

${JAVAC} -cp ${TX_SRC}/jango.jar:${TX_SRC}/TxSystem.jar:.: ${TX_SRC}/TxIntf/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#generate the replic stubs.
${JAVA} -cp ${TX_SRC}/jango.jar:${TX_SRC}/TxSystem.jar:.:${TX_SRC} jango.Jango TxIntf.TxnManagerIntf ${TX_SRC}/TxIntf 0 TxIntf
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

#Compile in the replic stubs too..
${JAVAC} -cp ${TX_SRC}/jango.jar:${TX_SRC}/TxSystem.jar:.:${TX_SRC} ${TX_SRC}/TxIntf/TxnManagerIntf_RepStub.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;


cd  ${TX_SRC}
jar cvf TxIntf.jar TxIntf/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

${JAVAC} -cp ${TX_SRC}/jango.jar:${TX_SRC}/TxSystem.jar:${TX_SRC}/TxIntf.jar:${TX_SRC}/G13Shared.jar ${TX_SRC}/TxImp/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;


echo '
grant codeBase "file:'${TX_SRC}/'" {
    permission java.security.AllPermission;
};
' >  ${SRC512_HOME}/tmanager.policy
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;


exit 0
