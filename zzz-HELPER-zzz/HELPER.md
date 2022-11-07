# Project Setup

---


## System hosts file

---

Add next lines to /etc/hosts file or to %WINDIR%\System32\drivers\etc\hosts if working with WSL:

```
# Event-Driven Microservices Course

127.0.0.1       postgres
127.0.0.1       keycloak-authorization-server
127.0.0.1       config-server
127.0.0.1       config-server-ha
127.0.0.1       elastic-1
127.0.0.1       elastic-2
127.0.0.1       elastic-3
127.0.0.1       elastic-query-service-1
127.0.0.1       elastic-query-service-2
127.0.0.1       elastic-query-web-client
127.0.0.1       discovery-service-1
127.0.0.1       discovery-service-2
127.0.0.1       analytics-service
127.0.0.1       kafka-streams-service
127.0.0.1       gateway-service-1
127.0.0.1       gateway-service-2
127.0.0.1       prometheus
127.0.0.1       grafana
127.0.0.1       zipkin
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

Enable max virtualized memory (for dockerized services in WSL, specifically for elastic cluster):

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

## MISC

---

### Config server:

For config server, we can either use **config-first** or **discovery-first** approach.
Previously, we have chosen to use config-first approach.

With **config-first** approach you can just add another instance and specify both instances in client's config server definition for high availability.
All other services must be updated with config server instance URIs, but second won't be used if first is up an running.

To use **discovery-first** approach we need to set spring.cloud.config.discovery.enabled variable to true in all clients. And make server a discover client.
This approach will require an extra round trip at start-up to fetch the config. But with it, adding new instances is easy as discovery will handle it automatically.

### Logback spring:

Update logback.xml files to get the app-name variable value to logback.

**IMPORTANT NOTE:**
- to be able to load spring configuration variable inside logback configuration, we need to rename logback files to logback-spring.xml files, so this logback configuration will be loaded after loading application configuration.

### Check Zipkin health:

> curl -s localhost:9411/health
