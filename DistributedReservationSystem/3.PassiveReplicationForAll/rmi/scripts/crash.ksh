#!/bin/ksh

. ${SRC512_HOME}/512_env

java -Djava.security.policy=${SRC512_HOME}/client.policy -cp ${CLIENT_SRC}/jango.jar:${CLIENT_SRC}/G13Shared.jar:${CLIENT_SRC}:${TX_SRC}/TxIntf.jar:${TX_SRC}/TxSystem.jar:${SERVER_SRC}/ResInterface.jar:${MIDW_SRC}/MWInterface.jar crashClient $@
