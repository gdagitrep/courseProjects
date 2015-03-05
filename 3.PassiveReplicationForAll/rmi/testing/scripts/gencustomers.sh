#!/bin/bash

. $SRC512_HOME/512_env

export startCustId=$1
export numCusts=$2
export fileName=$3

if [[ $# -ne 3 ]]
then
  echo "ERR: usage: $0 startcustid numberofcustomers filename"
	echo "      output will be stored in ${GENDATADIR}/filename.cmd"
	exit 1
fi

export highestCustId=0

let "highestCustId = $startCustId + $numCusts"
(
  echo start
	while [[ $startCustId -le $highestCustId ]]
	do
  	echo "newcustomerid,$startCustId"
		let "startCustId = $startCustId + 1"
	done
  echo commit
  echo quit
) > ${GENDATADIR}/${fileName}.cmd
