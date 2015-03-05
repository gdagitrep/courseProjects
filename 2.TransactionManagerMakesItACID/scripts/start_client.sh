#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env

java -Djava.security.policy=${SRC512_HOME}/client.policy -cp ${CLIENT_SRC}/G13Shared.jar:${CLIENT_SRC}:${CLIENT_SRC}/MWInterface.jar:${TX_SRC}/TxSystem.jar client $@
