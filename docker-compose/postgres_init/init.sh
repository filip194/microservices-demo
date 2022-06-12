#!/bin/bash

#############################################################################
# Bash Shell script to execute psql commands to create fitbitconnect database
#############################################################################

set -e
export PGPASSWORD=$POSTGRES_PASSWORD;
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER $KEYCLOAK_DB_USER WITH PASSWORD '$KEYCLOAK_DB_PASSWORD';
  ALTER USER $KEYCLOAK_DB_USER WITH SUPERUSER;
  ALTER USER $KEYCLOAK_DB_USER CREATEDB;
  ALTER USER $KEYCLOAK_DB_USER CREATEROLE;
  CREATE DATABASE $KEYCLOAK_DB;
  GRANT ALL PRIVILEGES ON DATABASE $KEYCLOAK_DB TO $KEYCLOAK_DB_USER;
EOSQL
