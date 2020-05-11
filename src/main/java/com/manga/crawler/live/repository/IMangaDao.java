package com.manga.crawler.live.repository;

import java.sql.SQLException;
import java.util.List;

public interface IMangaDao<ID, T, X, S, V> {

    T insertMangaSeries(T entity) throws SQLException;

    T findMangaSeriesById(ID id) throws SQLException;

    void createTableForMangaSeries(ID databaseName) throws SQLException;

    T updateDatabaseNameForMangaSeries(ID id, ID databaseName) throws SQLException;

    int updateMangaSeriesStatusByMangaName(X seriesStatus, ID mangaName) throws SQLException;

    List<T> findMangaSeriesByStatus(X mangaStatus) throws SQLException;

    List<ID> findAllMangaChaptersDownloaded(ID databaseName) throws SQLException;

    S insertMangaChapter(S entity, ID databaseName) throws SQLException;

    S findMangaChapterById(ID id, ID databaseName) throws SQLException;

    List<S> findMangaChapterByStatus(V chapterStatus, ID databaseName) throws SQLException;

    int updateMangaChapterStatus(ID databaseName, V chapterStatus, ID id) throws SQLException;

}
