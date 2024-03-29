#!/bin/bash
# check-config-server-started.sh

apt-get update -y
yes | apt-get install curl

curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health) # add security exception in SecurityConfig class to be able to reach this endpoint

echo "result status code: " "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  echo >&2 "Configuration server is not up yet!"
  sleep 2
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)
done

check-elastic-cluster-started.sh

check-keycloak-server-started.sh
