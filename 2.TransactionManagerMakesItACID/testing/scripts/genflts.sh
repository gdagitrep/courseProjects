#!/bin/bash

. $SRC512_HOME/512_env

export startFltId=$1
export numFlts=$2
export fileName=$3

if [[ $# -ne 3 ]]
then
  echo "ERR: usage: $0 startflightid numberofflights filename"
	echo "      output will be stored in ${GENDATADIR}/filename.cmd"
	exit 1
fi

minSeats=10
maxSeats=350

minPrice=200
maxPrice=2500

function printPrice()
{
	rnd=$RANDOM
  prce=$(echo "$rnd%$maxPrice+$minPrice" | bc)
	if [[ $prce -lt $minPrice ]]
	then
	  echo -e $minPrice
	elif [[ $prce -gt $maxPrice ]]
	then
	  echo -e $maxPrice
	else
	  echo -e $prce
	fi
}

function printSeats()
{
	rnd=$RANDOM
  rms=$(echo "$rnd%$maxSeats+$minSeats" | bc)
	if [[ $rms -lt $minSeats ]]
	then
	  echo -e $minSeats
	elif [[ $rms -gt $maxSeats ]]
	then
	  echo -e $maxSeats
	else
	  echo -e $rms
	fi
}


export highestFltId=0

let "highestFltId = $startFltId + $numFlts"
(
  echo start
	while [[ $startFltId -le $highestFltId ]]
	do
  	echo "newflight,$startFltId,"`printSeats`','`printPrice`
		let "startFltId = $startFltId + 1"
	done
  echo commit
  echo quit
) > ${GENDATADIR}/${fileName}.cmd
