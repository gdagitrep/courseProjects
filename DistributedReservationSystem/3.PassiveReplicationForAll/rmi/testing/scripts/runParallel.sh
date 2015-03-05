#!/bin/bash

. $SRC512_HOME/512_env

if [[ $# -lt 2 ]]
then
	echo "ERR: usage $0 hostsListFile remoteTestScript [args....]"
  exit 1
fi

hostFileList=$1
scriptName=$2
shift 2

date
for hst in $(cat ${hostFileList})
do
  ssh $hst ${TESTENVDIR}/scripts/$scriptName $@ &
	sleep 1
done
wait
date
