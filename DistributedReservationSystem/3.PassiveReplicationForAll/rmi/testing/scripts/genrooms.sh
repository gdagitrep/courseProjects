#!/bin/bash

. $SRC512_HOME/512_env

minRooms=30
maxRooms=600

minPrice=40
maxPrice=700

function printRooms()
{
	rnd=$RANDOM
  rms=$(echo "$rnd%$maxRooms+$minRooms" | bc)
	if [[ $rms -lt $minRooms ]]
	then
	  echo -e $minRooms
	elif [[ $rms -gt $maxRooms ]]
	then
	  echo -e $maxRooms
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
  	echo "newroom,City_$citycnt,"`printRooms`','`printPrice`
	  let "citycnt = $citycnt + 1"
	done
  echo commit
  echo quit
) > ${GENDATADIR}/rooms.cmd
