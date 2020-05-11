package com.manga.crawler.live.service;

import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.DirectoryFilePathUtils;
import com.manga.crawler.live.utils.ExceptionUtils;
import com.manga.crawler.live.utils.MangaSeriesStatusEnum;
import com.manga.crawler.live.utils.ParamsConst;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public class MangaSeriesService {

    private IMangaDao mangaDao;

    public MangaSeriesService(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
    }

    public MangaSeries setMangaSeriesToBeDownloaded(String mangaSeriesUrl, String mangaSeriesName) throws DaoException {
        // Set these MangaSeries related params to the Database
        MangaSeries mangaSeries = new MangaSeries();
        mangaSeries.setId(UUID.randomUUID().toString());
        mangaSeries.setMangaUrl(mangaSeriesUrl);
        mangaSeries.setMangaName(mangaSeriesName);
        mangaSeries.setStatus(MangaSeriesStatusEnum.ACTIVE);
        try {
            return (MangaSeries) mangaDao.insertMangaSeries(mangaSeries);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesById(mangaSeries.getId(), sqlex);
            return null;
        }
    }

    public void createTableForMangaSeries(String mangaDatabaseName) throws DaoException {
        try {
            mangaDao.createTableForMangaSeries(mangaDatabaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesById(mangaDatabaseName, sqlex);
        }
    }

    public MangaSeries updateDatabaseNameForMangaSeries(String id, String databaseName) throws DaoException {
        try {
            return (MangaSeries) mangaDao.updateDatabaseNameForMangaSeries(id, databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesById(id, sqlex);
            return null;
        }
    }

    public MangaSeries setMangaSeriesForDownload(String mangaSeriesUrl, String mangaSeriesName) throws DaoException {
        // Insert the MangaSeries record and get it for further processing
        ExceptionUtils.throwParamValidationExceptionIfBlank(ParamsConst.MANGA_URL, mangaSeriesUrl);
        ExceptionUtils.throwParamValidationExceptionIfBlank(ParamsConst.MANGA_NAME, mangaSeriesName);
        MangaSeries mangaSeries = null;
        try {
            mangaSeries = setMangaSeriesToBeDownloaded(mangaSeriesUrl, mangaSeriesName);
        } catch (DaoException daoex) {
            daoex.printStackTrace();
        }

        // Create a separate database for the MangaSeries
        String mangaDatabaseName = String.join("_", mangaSeriesName.toLowerCase().split(" "));
        try {
            createTableForMangaSeries(mangaDatabaseName);
            mangaSeries = updateDatabaseNameForMangaSeries(mangaSeries.getId(), mangaDatabaseName);
        } catch (DaoException daoex) {
            daoex.printStackTrace();
        }

        // Create a directory for the Manga Series
        if(!new File(DirectoryFilePathUtils.generateMangaSeriesDirectoryPath(mangaSeries.getMangaName())).mkdir()) {
            System.out.println(String.format("[ERROR] Could not create manga series directory: %s", mangaSeries.getMangaName()));
        }

        return mangaSeries;
    }


}
