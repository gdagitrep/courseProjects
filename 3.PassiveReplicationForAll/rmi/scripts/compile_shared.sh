#!/usr/bin/ksh

set -x

. $SRC512_HOME/512_env

${JAVAC} ${SHARED_SRC}/G13Log/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${JAVAC} ${SHARED_SRC}/G13Components/*.java
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

cd  ${SHARED_SRC}
jar cvf G13Shared.jar G13Log/*.class G13Components/*.class
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi; 
cd -

exit 0
