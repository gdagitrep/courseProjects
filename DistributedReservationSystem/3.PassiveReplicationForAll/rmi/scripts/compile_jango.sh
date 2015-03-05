#!/usr/bin/ksh

set -x

. $SRC512_HOME/512_env

${JAVAC} ${TOOLS}/jango/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${TOOLS}
jar cvf jango.jar jango/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

exit 0
