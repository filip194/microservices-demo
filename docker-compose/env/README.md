# Setup

---


## System hosts file

---

Add next lines to /etc/hosts file or to %WINDIR%\System32\drivers\etc\hosts if working with WSL:

```
# Event-Driven Microservices Course

127.0.0.1       keycloak-authorization-server
127.0.0.1       elastic-query-service-1
127.0.0.1       elastic-query-service-2
127.0.0.1       elastic-query-web-client-1
127.0.0.1       elastic-query-web-client-2
```

- /etc/hosts file will be overridden at each WSL startup beacuse it is generated from %WINDIR%\System32\drivers\etc\hosts, here is the solution: https://superuser.com/questions/1150597/linux-overrides-etc-hosts-on-windows-linux-subsystem

- in /etc/wsl.conf add next lines:

```
[network]
generateHosts = False
```

## Memory

---

Enable max virtualized memory (for dockerized services in WSL):

> sudo sysctl -w vm.max_map_count=262144

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
