package com.manga.crawler.live.utils;

public class SqlQueryConst {

  private SqlQueryConst() {
  }

  @Deprecated
  public final static String CREATE_MANGA_SERIES_DATABASE = "CREATE TABLE ? (Id NVARCHAR(64) NOT NULL, Chapter_Number NVARCHAR(64) NOT NULL, Chapter_Url NVARCHAR(256) NOT NULL, Status NVARCHAR(64) NOT NULL, PRIMARY KEY (Id), UNIQUE KEY `uc_manga_chapter` (`Chapter_Number`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";

  @Deprecated
  public final static String INSERT_MANGA_CHAPTER = "INSERT INTO ? (Id, Chapter_Number, Chapter_Url, Status) VALUES (?, ?, ?, ?)";

  @Deprecated
  public final static String UPDATE_MANGA_CHAPTER_STATUS = "UPDATE ? SET Status = ? WHERE Id = ?";

  @Deprecated
  public final static String FIND_MANGA_CHAPTER_BY_ID = "SELECT Id, Chapter_Number, Chapter_Url, Status FROM ? WHERE Id = ?";

  @Deprecated
  public final static String FIND_MANGA_CHAPTER_BY_STATUS = "SELECT Id, Chapter_Number, Chapter_Url, Status FROM ? WHERE Id = ?";

  public final static String INSERT_MANGA_SERIES = "INSERT INTO manga_list (Id, Manga_Url, Manga_Name, Status, Database_Name) VALUES (?, ?, ?, ?, ?)";

  public final static String UPDATE_MANGA_SERIES_STATUS_BY_ID = "UPDATE manga_list SET Status = ? WHERE Id = ?";

  public final static String UPDATE_MANGA_SERIES_STATUS_BY_NAME = "UPDATE manga_list SET Status = ? WHERE Manga_Name = ?";

  public final static String UPDATE_MANGA_SERIES_DATABASE_NAME = "UPDATE manga_list SET Database_Name = ? WHERE Id = ?";

  public final static String FIND_MANGA_SERIES = "SELECT Id, Manga_Url, Manga_Name, Status, Database_Name FROM manga_list WHERE Id = ?";

  public final static String FIND_MANGA_SERIES_TO_BE_DOWNLOADED = "SELECT Id, Manga_Url, Manga_Name, Status, Database_Name FROM manga_list WHERE Status = ?";

  @Deprecated
  public final static String FIND_ALL_MANGA_CHAPTERS_DOWNLOADED = "SELECT Chapter_Number FROM ?";

  public static String generateCreateChapterTableQuery(String databaseName) {
      return "CREATE TABLE IF NOT EXISTS `" + databaseName +  "` (Id NVARCHAR(64) NOT NULL, Chapter_Number NVARCHAR(64) NOT NULL, Chapter_Url NVARCHAR(256) NOT NULL, Status NVARCHAR(64) NOT NULL, PRIMARY KEY (Id), UNIQUE KEY `uc_manga_chapter` (`Chapter_Number`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
  }

  public static String generateInsertMangaChapter(String databaseName) {
      return "INSERT INTO `" + databaseName + "` (Id, Chapter_Number, Chapter_Url, Status) VALUES (?, ?, ?, ?)";
  }

  public static String generateUpdateMangaChapter(String databaseName) {
      return "UPDATE `" + databaseName + "` SET Status = ? WHERE Id = ?";
  }

  public static String generateFindMangaChapterById(String databaseName) {
      return "SELECT Id, Chapter_Number, Chapter_Url, Status FROM `" + databaseName + "` WHERE Id = ?";
  }

  public static String generateFindMangaChapterByStatus(String databaseName) {
      return "SELECT Id, Chapter_Number, Chapter_Url, Status FROM `" + databaseName + "` WHERE Status = ?";
  }

  public static String generateFindAllMangaChaptersDownloaded(String databaseName) {
      return "SELECT Chapter_Number FROM `" + databaseName + "`";
  }

}
