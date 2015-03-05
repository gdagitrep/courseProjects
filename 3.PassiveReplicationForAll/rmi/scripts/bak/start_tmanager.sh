#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

export LogDir=${LOGDIR}/${TEST_ID}/
mkdir -p ${LogDir}
export PROCESS_ID=${TMGR_RMI_OBJ_NAME}
export LogFile=${LogDir}/${TMGR_RMI_OBJ_NAME}.$(date '+%Y.%m.%d_%H.%M.%S').log

nohup java -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=file:///${TX_SRC}/ -Djava.security.policy=${SRC512_HOME}/tmanager.policy -cp ${TOOLS}/jango.jar:${TX_SRC}/TxIntf.jar:${TX_SRC}/TxSystem.jar:${TX_SRC}/G13Shared.jar:${TX_SRC}: TxImp.TxnManager  > ${LogFile} 2>&1 &
cpid=$!
#top -b  -d 1 -p $cpid | grep $cpid | awk -v hst=${HOSTNAME} -v processid=${PROCESS_ID} -v testid=${TEST_ID}  'BEGIN { OFS="|"; format = "%a %b %e %H:%M:%S %Z %Y" } { print "MEMCPU",strftime(format), hst, processid, testid,  $9,$10 }' > ${LogFile}.SYS 2>&1  &

sleep 5
#java -Djava.rmi.server.useCodebaseOnly=false  -Djava.security.policy=${SRC512_HOME}/tmanager.policy -cp ${TX_SRC}/TxIntf.jar:${TX_SRC}/TxSystem.jar:${TX_SRC}: TxImp.TxnManager
