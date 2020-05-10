#========================================================================>
# CREATE DATABASE
#========================================================================>
CREATE SCHEMA IF NOT EXISTS manga;

USE manga;

#========================================================================>
# CREATE TABLES
#========================================================================>
CREATE TABLE IF NOT EXISTS manga_list (
    Id            NVARCHAR(64) NOT NULL,
    Manga_Url     NVARCHAR(256) NOT NULL,
    Manga_Name    NVARCHAR(64) NOT NULL,
    Status        NVARCHAR(64) NOT NULL,
    Database_Name NVARCHAR(64),
    PRIMARY KEY (Id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;