#!/bin/bash
# check-keycloak-server-started.sh

# execute curl
curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://keycloak-authorization-server:9091/realms/microservices-realm)

echo "result status code: " "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  echo >&2 "Keycloak authorization server is not up yet!"
  sleep 2
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://keycloak-authorization-server:9091/realms/microservices-realm)
done

#./cnb/lifecycle/launcher # this launcher is deprecated as of new Spring Boot version > 2.5.x, use launcher below instead; process/web actually points to launcher
./cnb/process/web

#./check-elastic-cluster-started.sh
