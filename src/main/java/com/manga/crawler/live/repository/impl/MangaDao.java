package com.manga.crawler.live.repository.impl;

import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.MangaSeriesStatusEnum;
import com.manga.crawler.live.utils.SqlConnectionConst;
import com.manga.crawler.live.utils.SqlParamConst;
import com.manga.crawler.live.utils.SqlQueryConst;

import java.sql.*;

public class MangaDao implements IMangaDao<String, MangaSeries, MangaSeriesStatusEnum> {

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

}
