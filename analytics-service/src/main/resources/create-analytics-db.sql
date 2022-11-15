-- THIS SHOULD BE EXECUTED MANUALLY ON POSTGRES DB

CREATE SCHEMA IF NOT EXISTS analytics AUTHORIZATION postgres;

CREATE TABLE analytics.twitter_analytics
(
    id uuid NOT NULL,
    word CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    word_count BIGINT NOT NULL,
    record_date TIME WITH TIME ZONE,
    CONSTRAINT twitter_analytics_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;

ALTER TABLE analytics.twitter_analytics
    OWNER TO postgres;
--- Index: INDX_WORD_BY_DATE

--- DROP INDEX analytics."INDX_WORD_BY_DATE"

CREATE INDEX "INDX_WORD_BY_DATE"
    ON analytics.twitter_analytics USING btree
    (word COLLATE pg_catalog."default" ASC NULLS LAST, record_date DESC NULLS LAST)
    TABLESPACE pg_default;
