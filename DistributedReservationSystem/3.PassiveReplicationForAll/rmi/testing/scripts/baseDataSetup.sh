#!/bin/bash

. $SRC512_HOME/512_env

${SCRIPT_DIR}/start_clientb.sh 500 < /home/2013/jdsilv2/comp512/prj02/work01/rmi/testing/gendata/flt3000.cmd &
sleep 1
${SCRIPT_DIR}/start_clientb.sh 500 < /home/2013/jdsilv2/comp512/prj02/work01/rmi/testing/gendata/cars.cmd &
sleep 1
${SCRIPT_DIR}/start_clientb.sh 500 < /home/2013/jdsilv2/comp512/prj02/work01/rmi/testing/gendata/rooms.cmd &
sleep 1
${SCRIPT_DIR}/start_clientb.sh 500 < /home/2013/jdsilv2/comp512/prj02/work01/rmi/testing/gendata/cust10000.cmd &
wait
