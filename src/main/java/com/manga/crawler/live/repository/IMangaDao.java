package com.manga.crawler.live.repository;

import java.sql.SQLException;

public interface IMangaDao<ID, T, X> {

    T insertMangaSeries(T entity) throws SQLException;

    T findMangaSeriesById(ID id) throws SQLException;

    void createTableForMangaSeries(ID databaseName) throws SQLException;

    T updateDatabaseNameForMangaSeries(ID id, ID databaseName) throws SQLException;

}
