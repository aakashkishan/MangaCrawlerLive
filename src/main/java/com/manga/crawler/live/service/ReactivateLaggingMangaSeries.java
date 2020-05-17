package com.manga.crawler.live.service;

import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.DaoUtils;
import com.manga.crawler.live.utils.ExceptionUtils;
import com.manga.crawler.live.utils.MangaSeriesStatusEnum;
import com.manga.crawler.live.utils.SqlParamConst;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

public class ReactivateLaggingMangaSeries extends TimerTask {

    private IMangaDao mangaDao;

    public ReactivateLaggingMangaSeries(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
        new DaoUtils(this.mangaDao);
    }

    @SneakyThrows
    @Override
    public void run() {
        reactiveToBeUpdatedMangaSeries();
    }

    public void reactiveToBeUpdatedMangaSeries() throws IOException {

        System.out.println("Manga Reactivate Manga Service Starting!");
        List<MangaSeries> disabledMangaSeries = null;
        try {
            disabledMangaSeries = DaoUtils.findMangaSeriesByStatus(MangaSeriesStatusEnum.DISABLED);
        } catch (DaoException daoex) {
            System.out.println("DaoException: " + daoex);
        }

        for(MangaSeries manga: disabledMangaSeries) {
            List<String> downloadedChapters = null;
            try {
                downloadedChapters = DaoUtils.findTheDownloadedMangaChaptersOfMangaSeries(manga.getDatabaseName());
            } catch (DaoException daoex) {
                System.out.println("DaoException: " + daoex);
            }

            List<String> allChapters;
            allChapters = DaoUtils.getAllChaptersOfMangaSeries(manga);

            ExceptionUtils.throwParamValidationExceptionIfNull(SqlParamConst.LIST_OF_CHAPTERS, downloadedChapters);
            ExceptionUtils.throwParamValidationExceptionIfNull(SqlParamConst.LIST_OF_CHAPTERS, allChapters);

            Collections.sort(downloadedChapters);
            Collections.sort(allChapters);
            if(allChapters.equals(downloadedChapters)) {
                return;
            } else {
                try {
                    DaoUtils.updateMangaSeriesStatusByMangaName(MangaSeriesStatusEnum.ACTIVE, manga.getMangaName());
                } catch (DaoException daoex) {
                    System.out.println("DaoException: " + daoex);
                }
                System.out.println("The Manga Series has been Reactivated!");
            }
        }

    }

}
