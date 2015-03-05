#!/bin/bash

#cnt=1
#while [[ $cnt -le 50 ]]
#do
#  ssh lab2-${cnt} hostname
#	let "cnt = $cnt + 1"
#done

# is there a max hosts ?
if [[ -z "$1" ]]
then
  maxHosts=50
else
  maxHosts=$1
fi

# is there a max load average.
if [[ -z "$2" ]]
then
  maxLoad=10
else
  maxLoad=$2
fi


cnt=1
while [[ $cnt -le $maxHosts ]]
do
	export tgtHost=lab2-${cnt}
	ping -c 1 $tgtHost > /dev/null 2>&1
	rc=$?

	if [[ $rc -eq 0 ]]
	then
			  echo -n ${tgtHost}':'
        ssh ${tgtHost} uptime 2>/dev/null | awk '{ print $NF }'
	fi

	let "cnt = $cnt + 1"
done | awk -F ':'  "BEGIN { max="$maxLoad'; } { if ($NF  < max) { print $0; } }' | awk 'BEGIN { OFS=":"; } { print NR, $0; }'

