package com.manga.crawler.live.service;

import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.*;

import java.io.File;

public class MangaSeriesService {

    private IMangaDao mangaDao;

    public MangaSeriesService(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
        new DaoUtils(this.mangaDao);
    }

    public MangaSeries setMangaSeriesForDownload(String mangaSeriesUrl, String mangaSeriesName) throws DaoException {

        System.out.println("Manga Series Service Starting!");
        // Insert the MangaSeries record and get it for further processing
        ExceptionUtils.throwParamValidationExceptionIfBlank(ParamsConst.MANGA_URL, mangaSeriesUrl);
        ExceptionUtils.throwParamValidationExceptionIfBlank(ParamsConst.MANGA_NAME, mangaSeriesName);
        MangaSeries mangaSeries = null;
        try {
            mangaSeries = DaoUtils.setMangaSeriesToBeDownloaded(mangaSeriesUrl, mangaSeriesName);
        } catch (DaoException daoex) {
            System.out.println("DaoException!");
        }

        // Create a separate database for the MangaSeries
        String[] databaseStrs = mangaSeriesName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while(index < databaseStrs.length - 1) {
            sb.append(databaseStrs[index]).append("_");
        }
        sb.append(databaseStrs[index]);
        String mangaDatabaseName = sb.toString();

        try {
            DaoUtils.createTableForMangaSeries(mangaDatabaseName);
            mangaSeries = DaoUtils.updateDatabaseNameForMangaSeries(mangaSeries.getId(), mangaDatabaseName);
        } catch (DaoException daoex) {
            System.out.println("DaoException!");
        }

        // Create a directory for the Manga Series
        if(!new File(DirectoryFilePathUtils.generateMangaSeriesDirectoryPath(mangaSeries.getMangaName())).mkdir()) {
            System.out.println(String.format("[ERROR] Could not create manga series directory: %s", mangaSeries.getMangaName()));
        }

        return mangaSeries;
    }

}
