# Microservices Demo Project Setup

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
127.0.0.1       kibana
127.0.0.1       zipkin
```

- /etc/hosts file will be overridden at each WSL startup because it is generated from
  %WINDIR%\System32\drivers\etc\hosts, here is the
  solution: https://superuser.com/questions/1150597/linux-overrides-etc-hosts-on-windows-linux-subsystem

- in /etc/wsl.conf add next lines:

```
[network]
generateHosts = false
```

## Clone Config Server Repository

---

Navigate to microservices-demo/ directory and clone config server repository:

> git clone https://github.com/filip194/config-server-repository.git

## Manually create database tables

---

Create databases and tables from resource folders in next modules:

(open connection from pgAdmin to dockerized postgres and execute scripts from query tool)

- **elastic-query-service** (import tables and data into public schema)
    - ```init-schema.sql```
    - ```init-data.sql```
- **analytics-service** (creates new analytics schema and table)
    - ```create-anayltics-db.sql```

Without these databases and data, services can not work.

## Import data to enable microservices

---

### Import Keycloak data

You can either import partial realm settings (without users, and generally client secrets):

- In Keycloak UI on ```http://localhost:9091``` go to _Import_ or _Create Realm_ and select
  file ```realm-export-keycloak-v18.json``` from file browser.
- Missing from json file for this kind of export/import:
    - create users and assign user groups (and roles if needed)
    - recreate and re-encrypt client secrets and update them in config server

Better way to do this for this demo project, with all client secrets and users:

- connect to docker container: ```docker exec -it keycloak-authorization-server bin/sh```
- execute command: ```/opt/keycloak/bin/kc.sh import --dir /tmp/export --override true```

This way you should have all the data in Keycloak without any manual work.

#### ADDITIONAL: - keycloak realm export/import with users -

##### Keycloak v16 or lower:

Export/Import REALM and USERS:

- https://keepgrowing.in/tools/keycloak-in-docker-5-how-to-export-a-realm-with-users-and-secrets/
- https://keepgrowing.in/tools/keycloak-in-docker-6-how-to-import-realms-from-a-directory/

##### Keycloak v17 or higher:

- https://www.keycloak.org/server/importExport

Connect to docker container:
> docker exec -it keycloak-authorization-server bin/sh

Export REALM and USERS:

> /opt/keycloak/bin/kc.sh export --dir /tmp/export --realm microservices-realm --users different_files --users-per-file
> 10

Import REALM and USERS:

> /opt/keycloak/bin/kc.sh import --dir /tmp/export --override true

### Import Grafana graphs config

On http://grafana:3000 create new data source from prometheus:

- set URL as: ```http://prometheus:9090``` and save connection
- import file ```microservices-demo-grafana-export.json``` to see the graphs for microservices

### Import Kibana config (TODO)

## Enable/disable mock tweets

---

In ```docker-compose/services.yml``` file, under ```twitter-to-kafka-service``` change environment variable:

> TWITTER-TO-KAFKA-SERVICE_ENABLE-MOCK-TWEETS=false

Set ```true``` to enable mock tweets, or ```false``` to disable mock tweets and use real Twitter API to stream tweets.

- **NOTE:** If you are using real Twitter API, you have to set your system's env variable
  named: ```TWITTER_BEARER_TOKEN``` and give it value of your Twitter Bearer Token obtained
  from https://developer.twitter.com.

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
- b - brokers
- C - consumer
- t - topic

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

With **config-first** approach you can just add another instance and specify both instances in client's config server
definition for high availability.
All other services must be updated with config server instance URIs, but second won't be used if first is up an running.

To use **discovery-first** approach we need to set spring.cloud.config.discovery.enabled variable to true in all
clients. And make server a discover client.
This approach will require an extra round trip at start-up to fetch the config. But with it, adding new instances is
easy as discovery will handle it automatically.

### Logback spring:

Update logback.xml files to get the app-name variable value to logback.

**IMPORTANT NOTE:**

- to be able to load spring configuration variable inside logback configuration, we need to rename logback files to
  logback-spring.xml files, so this logback configuration will be loaded after loading application configuration.

### Check Zipkin health:

> curl -s localhost:9411/health

### Spring CLI

> sdk install springboot <optional_version>

> sdk version

> cd ~/.sdkman/candidates/

> sdk use springboot 2.7.5

> spring install org.springframework.cloud:spring-cloud-cli:3.1.1

> spring encrypt 'PLAIN_TEXT' --key '<_KEY_>'

> spring decrypt --key '<_KEY_>' 'PLAIN_TEXT'
