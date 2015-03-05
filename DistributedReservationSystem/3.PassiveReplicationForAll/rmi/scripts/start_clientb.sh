#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

if [[ $# -ne 1 ]]
then
  echo "ERR: usage $0 numTransactionsPerMinute"
	exit 1
fi

pid=$$

export LogDir=${LOGDIR}/${TEST_ID}/client
mkdir -p ${LogDir}
export PROCESS_ID=CLIENT
export LogFile=${LogDir}/${PROCESS_ID}.$pid.$(date '+%Y.%m.%d_%H.%M.%S').log
export lFile=/tmp/$(hostname).${LOGNAME}.$(basename $LogFile)

touch $lFile
nohup java -Djava.security.policy=${SRC512_HOME}/client.policy -cp ${CLIENT_SRC}/G13Shared.jar:${CLIENT_SRC}:${CLIENT_SRC}/MWInterface.jar:${TX_SRC}/TxSystem.jar client batch $1 | grep ^PRF > $lFile 2>&1

mv $lFile ${LogFile} >> ~/temp/msg.out 2>&1
sleep 2
