#!/usr/bin/ksh

set -x

. $SRC512_HOME/512_env

${SCRIPT_DIR}/compile_jango.sh
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${SCRIPT_DIR}/compile_shared.sh
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${SCRIPT_DIR}/compile_tx.sh
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${SCRIPT_DIR}/compile_server.sh
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${SCRIPT_DIR}/compile_mwlayer.sh
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${SCRIPT_DIR}/compile_client.sh
rc=$?; if [[ $rc -ne 0 ]]; then exit $rc; fi;

${SCRIPT_DIR}/fix_file_perm.sh

exit 0
