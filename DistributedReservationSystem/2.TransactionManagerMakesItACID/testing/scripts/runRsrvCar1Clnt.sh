#!/bin/bash

. $SRC512_HOME/512_env

export PROCESS_ID=CLIENT

date
${TESTENVDIR}/scripts/txnGenRsrvCar.sh 100000 10000 20000 | ${SCRIPT_DIR}/start_client.sh | grep '^PRF' > ${TESTENVDIR}/logs/CLIENT001/client/client_1.log
date
