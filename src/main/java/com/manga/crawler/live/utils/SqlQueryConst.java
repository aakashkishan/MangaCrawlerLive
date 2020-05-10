package com.manga.crawler.live.utils;

public class SqlQueryConst {

  private SqlQueryConst() {
  }

  public final static String CREATE_MANGA_SERIES_DATABASE = "CREATE TABLE IF NOT EXISTS ? ("
    + "Id NVARCHAR(64) NOT NULL, "
    + "Chapter_Number NVARCHAR(64) NOT NULL,"
    + "Chapter_Url NVARCHAR(256) NOT NULL,"
    + "Status NVARCHAR(64) NOT NULL,"
    + "PRIMARY KEY (Id)"
    + ")";

  public final static String INSERT_MANGA_CHAPTER = "INSERT INTO ? (Id, Chapter_Number, Chapter_Url, Status) VALUES (?, ?, ?, ?)";

  public final static String UPDATE_MANGA_CHAPTER = "UPDATE ? SET Status = ? WHERE Id = ?";

  public final static String FIND_MANGA_CHAPTER = "SELECT Id, Chapter_Number, Chapter_Url, Status FROM ? WHERE Id = ?";

  public final static String INSERT_MANGA_SERIES = "INSERT INTO manga_list (Id, Manga_Url, Manga_Name, Status, DatabaseName) VALUES (?, ?, ?, ?, ?)";

  public final static String UPDATE_MANGA_SERIES_STATUS = "UPDATE manga_list SET Status = ? WHERE Id = ?";

  public final static String UPDATE_MANGA_SERIES_DATABASE_NAME = "UPDATE manga_list SET Database_Name = ? WHERE Id = ?";

  public final static String FIND_MANGA_SERIES = "SELECT Id, Manga_Url, Manga_Name, Status, Database_Name FROM manga_list WHERE Id = ?";

}
