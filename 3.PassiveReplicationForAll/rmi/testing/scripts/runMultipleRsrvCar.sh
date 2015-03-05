#!/bin/bash

. $SRC512_HOME/512_env

if [[ $# -ne 3 ]]
then
	echo "ERR: usage $0 numberOfClients numberOfTxn txnRate"
  exit 1
fi

export numberOfClients=$1
export numberOfTxn=$2
export txnRate=$3

export PROCESS_ID=CLIENT

while [[ $numberOfClients -gt 0 ]]
do
nohup ${TESTENVDIR}/scripts/txnGenRsrvCar.sh ${numberOfTxn} 10000 20000 | ${SCRIPT_DIR}/start_clientb.sh ${txnRate}  | grep '^PRF' &
sleep 1
let "numberOfClients = $numberOfClients - 1"
done

sleep 5
wait
