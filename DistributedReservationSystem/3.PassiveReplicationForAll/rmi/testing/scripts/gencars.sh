#!/bin/bash

. $SRC512_HOME/512_env

minCars=30
maxCars=600

minPrice=40
maxPrice=700

function printCars()
{
	rnd=$RANDOM
  rms=$(echo "$rnd%$maxCars+$minCars" | bc)
	if [[ $rms -lt $minCars ]]
	then
	  echo -e $minCars
	elif [[ $rms -gt $maxCars ]]
	then
	  echo -e $maxCars
	else
	  echo -e $rms
	fi
}

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

citycnt=1
(
  echo start
	while [[ $citycnt -le 1000 ]]
	do
  	echo "newcar,City_$citycnt,"`printCars`','`printPrice`
	  let "citycnt = $citycnt + 1"
	done
  echo commit
  echo quit
) > ${GENDATADIR}/cars.cmd
