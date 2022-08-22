-- create USER
CREATE OR REPLACE FUNCTION create_user_if_not_exists() returns void AS $BODY$
BEGIN
   IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = '$POSTGRES_USER') THEN

      RAISE NOTICE 'Role/User "$POSTGRES_USER" already exists. Skipping.';
   ELSE
      BEGIN   -- nested block
         CREATE ROLE $POSTGRES_USER WITH PASSWORD '$POSTGRES_PASSWORD' SUPERUSER;
      EXCEPTION
         WHEN duplicate_object THEN
            RAISE NOTICE 'Role/User "$POSTGRES_USER" was just created by a concurrent transaction. Skipping.';
      END;
   END IF;
END;
$BODY$ LANGUAGE plpgsql;


-- create DB
CREATE OR REPLACE FUNCTION create_database_if_not_exists() returns void AS $BODY$
BEGIN
   IF EXISTS (
      SELECT FROM pg_catalog.pg_database
      WHERE  datname = '$KEYCLOAK_DB') THEN

      RAISE NOTICE 'Database "$KEYCLOAK_DB" already exists. Skipping.';
   ELSE
      BEGIN   -- nested block
         CREATE DATABASE $KEYCLOAK_DB;
      EXCEPTION
         WHEN duplicate_object THEN
            RAISE NOTICE 'Database "$KEYCLOAK_DB" was just created by a concurrent transaction. Skipping.';
      END;
   END IF;
END;
$BODY$ LANGUAGE plpgsql;

SELECT create_user_if_not_exists();
SELECT create_database_if_not_exists();

GRANT ALL PRIVILEGES ON DATABASE $KEYCLOAK_DB TO $KEYCLOAK_DB_USER;
