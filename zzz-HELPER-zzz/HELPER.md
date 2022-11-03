# Project Setup

---


## System hosts file

---

Add next lines to /etc/hosts file or to %WINDIR%\System32\drivers\etc\hosts if working with WSL:

```
# Event-Driven Microservices Course

127.0.0.1       postgres
127.0.0.1       keycloak-authorization-server
127.0.0.1       elastic-query-service-1
127.0.0.1       elastic-query-service-2
127.0.0.1       elastic-query-web-client-1
127.0.0.1       elastic-query-web-client-2
127.0.0.1       discovery-service-1
127.0.0.1       discovery-service-2
127.0.0.1       analytics-service
127.0.0.1       kafka-streams-service
127.0.0.1       gateway-service
127.0.0.1       prometheus
127.0.0.1       grafana
```

- /etc/hosts file will be overridden at each WSL startup because it is generated from %WINDIR%\System32\drivers\etc\hosts, here is the solution: https://superuser.com/questions/1150597/linux-overrides-etc-hosts-on-windows-linux-subsystem

- in /etc/wsl.conf add next lines:

```
[network]
generateHosts = false
```

## Import files

Import files for:

- Keycloak
- Grafana

## Memory

---

Enable max virtualized memory (for dockerized services in WSL):

> sudo sysctl -w vm.max_map_count=262144

## Executing .sh files

For Docker to be able to use .sh files they have to be in root group, so if needed use:

> sudo chown userName:root fileName

... and they have to have executable permissions:

> sudo chmod 755 fileName

## Kafkacat

---

- L - list
- b	- brokers
- C	- consumer
- t	- topic

> kafkacat -L -b HOST:PORT

> kafkacat -C -b HOST:PORT -t TOPIC_NAME


### WINDOWS (with Git Bash and Docker Desktop installed):

> docker run -it --network=host confluentinc/cp-kafkacat kafkacat -L -b localhost:19092

> docker run -it --network=host confluentinc/cp-kafkacat kafkacat -C -b localhost:19092 -t twitter-topic

### LINUX:

> kafkacat -L -b localhost:19092

> kafkacat -C -b localhost:19092 -t twitter-topic
