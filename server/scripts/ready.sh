#!/bin/bash
STATUS=-1

# 
# http://chimera.labs.oreilly.com/books/1234000001741/ch03.html#_creating_a_rule
# https://bintray.com/noamt/gradle-plugins/REST-Gradle-Plugin/view/read

for i in {1..60}; do
  STATUS=$(curl -k -o /dev/null --silent --write-out '%{http_code}\n' "$1")
  if [ $STATUS = "200" ]
  then
    echo " ready."
    exit 0;
  else
    echo -ne "."
    sleep 1
  fi
done

echo " timeout."
exit 1;


