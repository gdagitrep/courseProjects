#!/bin/bash

. ${SRC512_HOME}/512_env

for reg in start_car_rmiregistry.sh start_flt_rmiregistry.sh start_htl_rmiregistry.sh start_mw_rmiregistry.sh
do
  ${SCRIPT_DIR}/${reg} & 
done

wait