#!/bin/ksh

. ${SRC512_HOME}/512_env

${SCRIPT_DIR}/start_client.sh <<-END
newcar,1,Ottawa,50,35
newflight,1,239,300,1500
newflight,1,632,200,700
newroom,1,Ottawa,250,120
newcustomerid,1,10003
itinerary,1,10003,239,632,Ottawa,true,true
querycustomer,1,10003
quit
END
