#!/bin/ksh

set -x

. ${SRC512_HOME}/512_env
. ${SRC512_HOME}/512_fns.sh

gen_mw_replic_cfg

java -Djava.security.policy=${SRC512_HOME}/client.policy -cp ${CLIENT_SRC}/jango.jar:${CLIENT_SRC}/G13Shared.jar:${CLIENT_SRC}:${CLIENT_SRC}/MWInterface.jar:${TX_SRC}/TxSystem.jar client $@
