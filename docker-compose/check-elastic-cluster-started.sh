#!/bin/bash

apt-get update -y

yes | apt-get install curl

curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://elastic-1:9200)

echo "result status code:" "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  echo >&2 "Elastic cluster is not up yet!"
  sleep 2
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://elastic-1:9200)
done

./cnb/process/web # always required
