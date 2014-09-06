#!/bin/bash
JARFile="build/libs/seebie-server-0.0.1-SNAPSHOT.jar"
PIDFile="app.pid"
PID="-1"
NOPID="-1"

# http://java.dzone.com/articles/managing-spring-boot
# http://www.linuxjournal.com/content/return-values-bash-functions

function print_process {
  echo $(<"$PIDFile")
}

function check_pid_file {
  if [ -f $PIDFile ]
  then
    PID=$(print_process)
    return 0;
  else
    PID="-1"
    return 1;
  fi
}

function check_pid_running {
  check_pid_file
  if [ "$PID" == "$NOPID" ]
  then
    return 1    
  fi
  if ps -p $PID > /dev/null
  then
    return 0
  else
    return 1
  fi
}

case "$1" in

status)
  if check_pid_running
  then
    echo "Process is running (" $PID ")"
  else
    echo "Process not running"
  fi
;;

stop)

if check_pid_running
then
  kill -TERM $PID
  echo -ne "Stopping Process"
  NOT_KILLED=1
  for i in {1..30}; do
    if check_pid_running
    then
      echo -ne "."
      sleep 1
    else
      NOT_KILLED=0
    fi
  done
  echo
  if [ $NOT_KILLED = 1 ]
  then
    echo "Cannot kill process " $PID
    exit 1
  fi
  echo "Process stopped"
else
   echo "Process already stopped"
fi
;;

start)
  if check_pid_running
  then
    echo "Process already running"
    exit 1
  fi
  if [ ! -f $JARFile ]; then
    echo "Jar file not found"
    exit 1
  fi
  nohup java -jar $JARFile >/dev/null 2>&1 &
  echo "Process started"
;;

debug)
  if check_pid_running
  then
    echo "Process already running"
    exit 0
  fi
  if [ ! -f $JARFile ]; then
    echo "Jar file not found"
    exit 1
  fi
  nohup java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n -jar $JARFile >/dev/null 2>&1 &

  echo "Process started"
;;

restart)
  $0 stop
  if [ $? = 1 ]
  then
    exit 1
  fi
  $0 start
;;

*)
  echo "Usage: $0 {start|stop|restart|debug|status}"
  exit 1

esac

exit 0


