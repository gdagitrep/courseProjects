#!/bin/bash

. $SRC512_HOME/512_env

export numReservations=$1
export minCustId=$2
export maxCustId=$3
export fileName=$4

if [[ $# -ne 3 ]]
then
  echo "ERR: usage: $0 numReservations minCustId maxCustId"
	exit 1
fi

function printCustId()
{
	rnd=$RANDOM
  cust=$(echo "$rnd%$maxCustId+$minCustId" | bc)
	if [[ $cust -lt $minCustId ]]
	then
	  echo -e $minCustId
	elif [[ $cust -gt $maxCustId ]]
	then
	  echo -e $maxCustId
	else
	  echo -e $cust
	fi
}

numCities=$(wc -l < $TESTBASEDATADIR/cities.txt)
function printCityIndex()
{
	rnd=$RANDOM
  cidx=$(echo "$rnd%$numCities+1" | bc)
	if [[ $cidx -lt $1 ]]
	then
	  echo -e 1
	elif [[ $cidx -gt $numCities ]]
	then
	  echo -e $numCities
	else
	  echo -e $cidx
	fi
}

maxLocId=1000
minLocId=1
function getLocation()
{
	rnd=$RANDOM
  loc=$(echo "$rnd%$maxLocId+$minLocId" | bc)
	if [[ $loc -lt $minLocId ]]
	then
	  c_idx=$minLocId
	elif [[ $loc -gt $maxLocId ]]
	then
	  c_idx=$maxLocId
	else
	  c_idx=$loc
	fi
	echo -e City_${c_idx}

}

export cnt=0
(
	while [[ $cnt -lt $numReservations ]]
	do
		custId=`printCustId`
		Location=`getLocation`
		echo "start"
  	echo "reserveCar,$custId,$Location"
		echo "commit"
		let "cnt = $cnt + 1"
	done
  echo quit
)
