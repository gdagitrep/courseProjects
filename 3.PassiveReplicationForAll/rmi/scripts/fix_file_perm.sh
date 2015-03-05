#!/usr/bin/ksh

. ${SRC512_HOME}/512_env

find ${SRC512_HOME}/ -type f -name '*.java' | xargs chmod 600
find ${SRC512_HOME}/ -type f -name '*.sh' | xargs chmod 700

umask 022
find ${SRC512_HOME}/ -type d | xargs chmod +rx
find ${SRC512_HOME}/ -type f -name '*.class' -o -name '*.jar' -o -name '*.policy' | xargs chmod +r
