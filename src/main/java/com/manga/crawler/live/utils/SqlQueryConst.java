package com.manga.crawler.live.utils;

public class SqlQueryConst {

  private SqlQueryConst() {
  }

  public final static String CREATE_MANGA_SERIES_DATABASE = "CREATE TABLE IF NOT EXISTS ? ("
    + "Id NVARCHAR(64) NOT NULL, "
    + "Chapter_Number NVARCHAR(64) NOT NULL,"
    + "Chapter_Url NVARCHAR(256) NOT NULL,"
    + "Status NVARCHAR(64) NOT NULL,"
    + "PRIMARY KEY (Id),"
    + "UNIQUE KEY `uc_manga_chapter` (`Chapter_Number`)"
    + ")";

  public final static String INSERT_MANGA_CHAPTER = "INSERT INTO ? (Id, Chapter_Number, Chapter_Url, Status) VALUES (?, ?, ?, ?)";

  public final static String UPDATE_MANGA_CHAPTER_STATUS = "UPDATE ? SET Status = ? WHERE Id = ?";

  public final static String FIND_MANGA_CHAPTER_BY_ID = "SELECT Id, Chapter_Number, Chapter_Url, Status FROM ? WHERE Id = ?";

  public final static String FIND_MANGA_CHAPTER_BY_STATUS = "SELECT Id, Chapter_Number, Chapter_Url, Status FROM ? WHERE Status = ?";

  public final static String INSERT_MANGA_SERIES = "INSERT INTO manga_list (Id, Manga_Url, Manga_Name, Status, DatabaseName) VALUES (?, ?, ?, ?, ?)";

  public final static String UPDATE_MANGA_SERIES_STATUS_BY_ID = "UPDATE manga_list SET Status = ? WHERE Id = ?";

  public final static String UPDATE_MANGA_SERIES_STATUS_BY_NAME = "UPDATE manga_list SET Status = ? WHERE Manga_Name = ?";

  public final static String UPDATE_MANGA_SERIES_DATABASE_NAME = "UPDATE manga_list SET Database_Name = ? WHERE Id = ?";

  public final static String FIND_MANGA_SERIES = "SELECT Id, Manga_Url, Manga_Name, Status, Database_Name FROM manga_list WHERE Id = ?";

  public final static String FIND_MANGA_SERIES_TO_BE_DOWNLOADED = "SELECT Id, Manga_Url, Manga_Name, Status, Database_Name FROM manga_list WHERE Status = ?";

  public final static String FIND_ALL_MANGA_CHAPTERS_DOWNLOADED = "SELECT Chapter_Number FROM ?";

}
