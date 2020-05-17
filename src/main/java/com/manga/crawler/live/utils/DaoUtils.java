package com.manga.crawler.live.utils;

import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.model.MangaChapter;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jsoup.Jsoup.connect;

public class DaoUtils {

    private static IMangaDao mangaDao;

    public DaoUtils(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
    }

    /*
     * ===============================
     * = MANGA CHAPTER SERVICE UTILS =
     * ===============================
     */

    public static List<MangaSeries> findMangaSeriesByStatus(MangaSeriesStatusEnum mangaStatus) throws DaoException {
        try {
            return mangaDao.findMangaSeriesByStatus(mangaStatus);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesByStatus(mangaStatus, sqlex);
            return null;
        }
    }

    public static List<String> findTheDownloadedMangaChaptersOfMangaSeries(String databaseName) throws DaoException {
        try {
            return mangaDao.findAllMangaChaptersDownloaded(databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoException(sqlex);
            return null;
        }
    }

    public static List<String> getAllChaptersOfMangaSeries(MangaSeries mangaSeries) throws IOException {
        List<String> allChapters = new ArrayList<>();
        Document doc = connect(mangaSeries.getMangaUrl()).get();
        Element mangaChaptersTable = doc.getElementById(WebScrapeConst.MANGA_CHAPTERS_CLASS);
        Elements mangaChapters = mangaChaptersTable.select(WebScrapeConst.ANCHOR_TAG);
        for(Element mangaChapter: mangaChapters) {
            allChapters.add(String.format("%s%s", WebScrapeConst.BASE_URL, mangaChapter.attr(WebScrapeConst.HREF_ATTRIBUTE)));
        }
        return allChapters;
    }

    public static List<String> getYetToBeDownloadedChapterNumbers(List<String> allChapters, List<String> downloadedChapterNumbers) {
        List<String> toBeDownloadedChapterNumbers = new ArrayList<>();
        for(String webChapterNumber: allChapters) {
            boolean isFound = false;
            for(String downloadedChapterNumber: downloadedChapterNumbers) {
                if(webChapterNumber.equals(downloadedChapterNumber)) {
                    isFound = true;
                }
            }
            if(isFound == false) {
                toBeDownloadedChapterNumbers.add(webChapterNumber);
            }
        }
        return toBeDownloadedChapterNumbers;
    }

    public static MangaChapter insertChapterForMangaSeries(MangaChapter mangaChapter, String databaseName) throws DaoException {
        try {
            return (MangaChapter) mangaDao.insertMangaChapter(mangaChapter, databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaChapterById(mangaChapter.getId(), sqlex);
            return null;
        }
    }

    public static List<MangaChapter> findMangaChaptersToBeDownloaded(MangaChapterStatusEnum chapterStatus, String databaseName) {
        try {
            return mangaDao.findMangaChapterByStatus(chapterStatus, databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaChapterByStatus(chapterStatus, sqlex);
            return null;
        }
    }

    public static int updateMangaChapterStatus(String databaseName, MangaChapterStatusEnum mangaStatus, String id) throws DaoException {
        try {
            return mangaDao.updateMangaChapterStatus(databaseName, mangaStatus, id);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaChapterByStatus(mangaStatus, sqlex);
            return -1;
        }
    }

    public static int updateMangaSeriesStatusByMangaName(MangaSeriesStatusEnum seriesStatus, String mangaName) throws DaoException {
        try {
            return mangaDao.updateMangaSeriesStatusByMangaName(seriesStatus, mangaName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesByMangaName(mangaName, sqlex);
            return -1;
        }
    }

    /*
     * ===============================
     * = MANGA SERIES SERVICE UTILS =
     * ===============================
     */

    public static MangaSeries setMangaSeriesToBeDownloaded(String mangaSeriesUrl, String mangaSeriesName) throws DaoException {
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

    public static void createTableForMangaSeries(String mangaDatabaseName) throws DaoException {
        try {
            mangaDao.createTableForMangaSeries(mangaDatabaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesById(mangaDatabaseName, sqlex);
        }
    }

    public static MangaSeries updateDatabaseNameForMangaSeries(String id, String databaseName) throws DaoException {
        try {
            return (MangaSeries) mangaDao.updateDatabaseNameForMangaSeries(id, databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesById(id, sqlex);
            return null;
        }
    }

}
