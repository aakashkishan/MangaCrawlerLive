package com.manga.crawler.live.repository.impl;

import com.manga.crawler.live.model.MangaChapter;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MangaDao implements IMangaDao<String, MangaSeries, MangaSeriesStatusEnum, MangaChapter, MangaChapterStatusEnum> {

    @Override
    public MangaSeries insertMangaSeries(MangaSeries mangaSeries) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.INSERT_MANGA_SERIES);) {
            preparedStatement.setString(1, mangaSeries.getId());
            preparedStatement.setString(2, mangaSeries.getMangaUrl());
            preparedStatement.setString(3, mangaSeries.getMangaName());
            preparedStatement.setString(4, mangaSeries.getStatus().toString());
            preparedStatement.setString(5, mangaSeries.getDatabaseName());
            preparedStatement.executeUpdate();
            return findMangaSeriesById(mangaSeries.getId());
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public MangaSeries findMangaSeriesById(String id) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.FIND_MANGA_SERIES);) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            MangaSeries mangaSeries = new MangaSeries();
            while(resultSet.next()) {
                mangaSeries.setId(resultSet.getString(SqlParamConst.ID));
                mangaSeries.setMangaUrl(resultSet.getString(SqlParamConst.MANGA_URL));
                mangaSeries.setMangaName(resultSet.getString(SqlParamConst.MANGA_NAME));
                mangaSeries.setStatus(MangaSeriesStatusEnum.valueOf(resultSet.getString(SqlParamConst.STATUS)));
                mangaSeries.setDatabaseName(resultSet.getString(SqlParamConst.DATABASE_NAME));
            }
            return mangaSeries;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public void createTableForMangaSeries(String databaseName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.CREATE_MANGA_SERIES_DATABASE);) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public MangaSeries updateDatabaseNameForMangaSeries(String id, String databaseName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.UPDATE_MANGA_SERIES_DATABASE_NAME);) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
            return findMangaSeriesById(id);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public int updateMangaSeriesStatusByMangaName(MangaSeriesStatusEnum seriesStatus, String mangaName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.UPDATE_MANGA_SERIES_STATUS_BY_NAME);) {
            preparedStatement.setString(1, seriesStatus.toString());
            preparedStatement.setString(2, mangaName);
            return preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public List<MangaSeries> findMangaSeriesByStatus(MangaSeriesStatusEnum mangaStatus) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.FIND_MANGA_SERIES_TO_BE_DOWNLOADED);) {
            preparedStatement.setString(1, mangaStatus.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<MangaSeries> listOfMangas = new ArrayList<>();
            while(resultSet.next()) {
                MangaSeries mangaSeries = new MangaSeries();
                mangaSeries.setId(resultSet.getString(SqlParamConst.ID));
                mangaSeries.setMangaUrl(resultSet.getString(SqlParamConst.MANGA_URL));
                mangaSeries.setMangaName(resultSet.getString(SqlParamConst.MANGA_NAME));
                mangaSeries.setStatus(MangaSeriesStatusEnum.valueOf(resultSet.getString(SqlParamConst.STATUS)));
                mangaSeries.setDatabaseName(resultSet.getString(SqlParamConst.DATABASE_NAME));
                listOfMangas.add(mangaSeries);
            }
            return listOfMangas;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public List<String> findAllMangaChaptersDownloaded(String databaseName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.FIND_ALL_MANGA_CHAPTERS_DOWNLOADED);) {
            preparedStatement.setString(1, databaseName);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> listOfChapters = new ArrayList<>();
            while(resultSet.next()) {
                listOfChapters.add(resultSet.getString(SqlParamConst.CHAPTER_NUMBER));
            }
            return listOfChapters;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public MangaChapter insertMangaChapter(MangaChapter mangaChapter, String databaseName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.INSERT_MANGA_CHAPTER);) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, mangaChapter.getId());
            preparedStatement.setString(3, mangaChapter.getChapterNumber());
            preparedStatement.setString(4, mangaChapter.getChapterUrl());
            preparedStatement.setString(5, mangaChapter.getStatus().toString());
            preparedStatement.executeUpdate();

            return findMangaChapterById(mangaChapter.getId(), databaseName);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public MangaChapter findMangaChapterById(String id, String databaseName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.FIND_MANGA_CHAPTER_BY_ID);) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            MangaChapter mangaChapter = new MangaChapter();
            while(resultSet.next()) {
                mangaChapter.setId(resultSet.getString(SqlParamConst.ID));
                mangaChapter.setChapterNumber(resultSet.getString(SqlParamConst.CHAPTER_NUMBER));
                mangaChapter.setChapterUrl(resultSet.getString(SqlParamConst.CHAPTER_URL));
                mangaChapter.setStatus(MangaChapterStatusEnum.valueOf(resultSet.getString(SqlParamConst.STATUS)));
            }
            return mangaChapter;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public List<MangaChapter> findMangaChapterByStatus(MangaChapterStatusEnum chapterStatus, String databaseName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.FIND_MANGA_CHAPTER_BY_STATUS);) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, chapterStatus.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<MangaChapter> listOfChapters = new ArrayList<>();
            while(resultSet.next()) {
                MangaChapter mangaChapter = new MangaChapter();
                mangaChapter.setId(resultSet.getString(SqlParamConst.ID));
                mangaChapter.setChapterNumber(resultSet.getString(SqlParamConst.CHAPTER_NUMBER));
                mangaChapter.setChapterUrl(resultSet.getString(SqlParamConst.CHAPTER_URL));
                mangaChapter.setStatus(MangaChapterStatusEnum.valueOf(resultSet.getString(SqlParamConst.STATUS)));
                listOfChapters.add(mangaChapter);
            }
            return listOfChapters;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

    @Override
    public int updateMangaChapterStatus(String databaseName, MangaChapterStatusEnum mangaStatus, String id) throws SQLException {
        try (Connection conn = DriverManager.getConnection(SqlConnectionConst.URL, SqlConnectionConst.USERNAME, SqlConnectionConst.PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(SqlQueryConst.UPDATE_MANGA_CHAPTER_STATUS);) {
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, mangaStatus.toString());
            preparedStatement.setString(3, id);
            return preparedStatement.executeUpdate();
        } catch(SQLException sqlex) {
            sqlex.printStackTrace();
            throw sqlex;
        }
    }

}
