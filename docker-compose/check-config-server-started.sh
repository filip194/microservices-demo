#!/bin/bash
# check-config-server-started.sh

# install curl command line utility
apt-get update -y
yes | apt-get install curl

# execute curl
curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health) # add security exception in SecurityConfig class to be able to reach this endpoint

echo "result status code: " "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  >&2 echo "Configuration server is not up yet!"
  sleep 2
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)
done

#./cnb/lifecycle/launcher # this launcher is deprecated as of new Spring Boot version > 2.5.x, use launcher below instead; process/web actually points to launcher
./cnb/process/web