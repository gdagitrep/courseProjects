#!/bin/bash

. $SRC512_HOME/512_env

export numReservations=$1
export minCustId=$2
export maxCustId=$3
export minFlightId=$4
export maxFlightId=$5
export fileName=$6

if [[ $# -ne 6 ]]
then
  echo "ERR: usage: $0 numReservations minCustId maxCustId minFlightId maxFlightId filename"
	echo "      output will be stored in ${GENDATADIR}/filename.cmd"
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

function printFlightId()
{
	rnd=$RANDOM
  flt=$(echo "$rnd%$maxFlightId+$minFlightId" | bc)
	if [[ $flt -lt $minFlightId ]]
	then
	  echo -e $minFlightId
	elif [[ $flt -gt $maxFlightId ]]
	then
	  echo -e $maxFlightId
	else
	  echo -e $flt
	fi
}

minFlts=1
maxFlts=4
function printFlights()
{
	rnd=$RANDOM
  flts=$(echo "$rnd%$maxFlts+$minFlts" | bc)
	if [[ $flts -lt $minFlts ]]
	then
	  flts=$minFlts
	elif [[ $flts -gt $maxFlts ]]
	then
	  flts=$maxFlts
	else
	  flts=$flts
	fi

	fltStr=""
	while [[ $flts -gt 0 ]]
	do
		fltStr=`printFlightId`",""$fltStr"
	  let "flts = $flts - 1"
	done

	echo -e $fltStr
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
function getLocation()
{
	c_idx=`printCityIndex`
  awk -v c_idx=$c_idx '{ if(NR==c_idx) { print $0; } }' < $TESTBASEDATADIR/cities.txt
}

function pickTrueOrFalse()
{
	rnd=$RANDOM
  chox=$(echo "$rnd%2" | bc)
	if [[ $chox -eq 1 ]]
	then
	  echo -e true
	else
	  echo -e false
	fi
}

function queryFlights()
{
  for flt in $(echo $@ | awk -F ',' '{ fld=1; while (fld<=NF) { print $fld; fld = fld + 1; } }')
	do
		if [[ -z "$flt" ]]
		then
		  continue
	  fi
	  echo queryflight,$flt
	done
}

export cnt=0
(
	while [[ $cnt -lt $numReservations ]]
	do
		custId=`printCustId`
		wantCar=`pickTrueOrFalse`
		wantRoom=`pickTrueOrFalse`
		Location=`getLocation`
		FlightList=`printFlights`
		echo "start"
		echo "querycustomer,$custId"

		if [[ $wantCar = "true" ]]
		then
		  echo "querycar,$Location"
		fi
		if [[ $wantRoom = "true" ]]
		then
		  echo "queryroom,$Location"
		fi

  	echo "itinerary,$custId,"${FlightList}$Location",${wantCar},${wantRoom}"

		if [[ $wantCar = "true" ]]
		then
		  echo "querycar,$Location"
		fi
		if [[ $wantRoom = "true" ]]
		then
		  echo "queryroom,$Location"
		fi

		queryFlights ${FlightList}

		echo "querycustomer,$custId"
		echo "commit"
		let "cnt = $cnt + 1"
	done
  echo quit
) > ${GENDATADIR}/${fileName}.cmd
