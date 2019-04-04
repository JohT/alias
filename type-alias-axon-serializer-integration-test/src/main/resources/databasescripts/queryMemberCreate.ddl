-- This script uses H2 Database specific language extensions "IF NOT EXISTS" and "MERGE INTO .. KEY" to make it idempotent

CREATE TABLE IF NOT EXISTS ACCOUNT (ACCOUNTID VARCHAR, NICKNAME VARCHAR, PRIMARY KEY (ACCOUNTID));
