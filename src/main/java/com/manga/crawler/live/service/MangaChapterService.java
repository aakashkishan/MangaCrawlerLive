package com.manga.crawler.live.service;

import com.itextpdf.text.DocumentException;
import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.model.MangaChapter;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.*;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jsoup.Jsoup.connect;

public class MangaChapterService {

    private IMangaDao mangaDao;

    public MangaChapterService(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
    }

    public List<MangaSeries> findMangaSeriesToBeDownloaded(MangaSeriesStatusEnum mangaStatus) throws DaoException {
        try {
            return mangaDao.findMangaSeriesByStatus(mangaStatus);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesByStatus(mangaStatus, sqlex);
            return null;
        }
    }

    public List<String> findTheDownloadedMangaChaptersOfMangaSeries(String databaseName) throws DaoException {
        try {
            return mangaDao.findAllMangaChaptersDownloaded(databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoException(sqlex);
            return null;
        }
    }

    public List<String> getAllChaptersOfMangaSeries(MangaSeries mangaSeries) throws IOException {
        List<String> allChapters = new ArrayList<>();
        Document doc = connect(mangaSeries.getMangaUrl()).get();
        Element mangaChaptersTable = doc.getElementById(WebScrapeConst.MANGA_CHAPTERS_CLASS);
        Elements mangaChapters = mangaChaptersTable.select(WebScrapeConst.ANCHOR_TAG);
        for(Element mangaChapter: mangaChapters) {
            allChapters.add(String.format("%s%s", WebScrapeConst.BASE_URL, mangaChapter.attr(WebScrapeConst.HREF_ATTRIBUTE)));
        }
        return allChapters;
    }

    public List<String> getYetToBeDownloadedChapterNumbers(List<String> allChapters, List<String> downloadedChapterNumbers) {
        List<String> toBeDownloadedChapterNumbers = new ArrayList<>();
        for(String webChapterNumber: allChapters) {
            boolean isFound = false;
            for(String downloadedChapterNumber: downloadedChapterNumbers) {
                if(webChapterNumber == downloadedChapterNumber) {
                    isFound = true;
                }
            }
            if(isFound == false) {
                toBeDownloadedChapterNumbers.add(webChapterNumber);
            }
        }
        return toBeDownloadedChapterNumbers;
    }

    public MangaChapter insertChapterForMangaSeries(MangaChapter mangaChapter, String databaseName) throws DaoException {
        try {
            return (MangaChapter) mangaDao.insertMangaChapter(mangaChapter, databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaChapterById(mangaChapter.getId(), sqlex);
            return null;
        }
    }

    public List<MangaChapter> findMangaChaptersToBeDownloaded(MangaChapterStatusEnum chapterStatus, String databaseName) {
        try {
            return mangaDao.findMangaChapterByStatus(chapterStatus, databaseName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaChapterByStatus(chapterStatus, sqlex);
            return null;
        }
    }

    public int updateMangaChapterStatus(String databaseName, MangaChapterStatusEnum mangaStatus, String id) throws DaoException {
        try {
            return mangaDao.updateMangaChapterStatus(databaseName, mangaStatus, id);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaChapterByStatus(mangaStatus, sqlex);
            return -1;
        }
    }

    public int updateMangaSeriesStatusByMangaName(MangaSeriesStatusEnum seriesStatus, String mangaName) throws DaoException {
        try {
            return mangaDao.updateMangaSeriesStatusByMangaName(seriesStatus, mangaName);
        } catch (SQLException sqlex) {
            ExceptionUtils.throwDaoExceptionForMangaSeriesByMangaName(mangaName, sqlex);
            return -1;
        }
    }

    public void downloadMangaChapters(List<MangaChapter> listOfChaptersToBeDownloaded, String databaseName, String mangaName) throws IOException, DocumentException, DaoException {
        for(MangaChapter mangaChapter: listOfChaptersToBeDownloaded) {
            // Scrape match the DOM elements for the chapter pages
            List<String> chapterPagesUrls = new ArrayList<>();
            Document chapterDoc = connect(mangaChapter.getChapterUrl()).get();
            Element chapterPagesDiv = chapterDoc.getElementById(WebScrapeConst.SELECT_DIV);
            Elements chapterPagesOptions = chapterPagesDiv.select(WebScrapeConst.OPTION_TAG);
            for(Element chapterPageOption: chapterPagesOptions) {
                chapterPagesUrls.add(String.format("%s%s", WebScrapeConst.BASE_URL, chapterPageOption.attr(WebScrapeConst.VALUE_ATTRIBUTE)));
            }

            // Setup a Directory for the Chapter
            if(!new File(DirectoryFilePathUtils.generateMangaChapterDirectoryPathForMangaSeries(mangaChapter.getChapterNumber(), mangaName)).mkdir()) {
                System.out.println(String.format("[ERROR] Could not create manga chapter directory: %s", String.format("Chapter %s", mangaChapter.getChapterNumber())));
            }

            Pattern p = Pattern.compile("(\\d+)(?!.*\\d)");
            System.out.println(chapterPagesUrls);
            boolean firstPage = true;
            for(String chapterPageUrl: chapterPagesUrls) {
                // Get the PageNumber using Regex
                if(firstPage) {
                    chapterPageUrl = new StringBuilder(chapterPageUrl).append("/1").toString();
                    firstPage = false;
                }
                Matcher m = p.matcher(chapterPageUrl);
                String pageNumber = null;
                if(m.find()) {
                    pageNumber = m.group();
                }
                Document chapterPageDoc = connect(chapterPageUrl).get();
                Element chapterPageImageDiv = chapterPageDoc.getElementById(WebScrapeConst.IMAGE_DIV);
                Element chapterPageImageTag = chapterPageImageDiv.selectFirst(WebScrapeConst.IMAGE_TAG);
                String chapterPageImageUrl = chapterPageImageTag.absUrl(WebScrapeConst.IMAGE_SRC_ATTRIBUTE);
                System.out.println(chapterPageImageUrl);

                // Download the Chapter Page
                File mangaChapterPageImage = new File(DirectoryFilePathUtils.generatePageFilePathForMangaChapterInMangaSeries(pageNumber, mangaChapter.getChapterNumber(), mangaName));
                FileOutputStream out = new FileOutputStream(DirectoryFilePathUtils.generatePageFilePathForMangaChapterInMangaSeries(pageNumber, mangaChapter.getChapterNumber(), mangaName));

                // Query the Image Source
                Connection.Response mangaChapterPage = connect(chapterPageImageUrl).ignoreContentType(true).maxBodySize(0).execute();
                out.write(mangaChapterPage.bodyAsBytes());
                out.close();
            }

            new PngToPdfConverter().convertPngToPdf(DirectoryFilePathUtils.generateMangaChapterDirectoryPathForMangaSeries(mangaChapter.getChapterNumber(), mangaName),
                    mangaChapter.getChapterNumber(), mangaName);

            // Set the Status of the Chapter to DISABLED once it is downloaded
            try {
                updateMangaChapterStatus(databaseName, MangaChapterStatusEnum.DISABLED, mangaChapter.getId());
            } catch (DaoException daoex) {
                System.out.println("DaoException!");
            }
        }

        // Set the Status of the Manga Series to DISABLED once all chapters are downloaded
        try {
            updateMangaSeriesStatusByMangaName(MangaSeriesStatusEnum.DISABLED, mangaName);
        } catch (DaoException daoex) {
            System.out.println("DaoException!");
        }
    }

    public void mangaChapterService() throws IOException, DaoException {
        List<MangaSeries> listOfMangas = null;
        try {
            listOfMangas = findMangaSeriesToBeDownloaded(MangaSeriesStatusEnum.ACTIVE);
        } catch (DaoException daoex) {
            System.out.println("DaoException!");
        }

        for(MangaSeries mangaSeries: listOfMangas) {
            // Get the already downloaded chapters for the manga-series
            List<String> downloadedChapterNumbers = new ArrayList<>();
            try {
                downloadedChapterNumbers = findTheDownloadedMangaChaptersOfMangaSeries(mangaSeries.getDatabaseName());
            } catch (DaoException daoex) {
                System.out.println("DaoException!");
            }

            // Get all chapters for the manga series
            List<String> allChapterUrls = getAllChaptersOfMangaSeries(mangaSeries);
            Map<String, String> allChapterMap = new HashMap<>();
            // Process the data to obtain a list of chapter numbers
            Pattern p = Pattern.compile("(\\d+)(?!.*\\d)");
            List<String> allChapters = new ArrayList<>();
            for(String chapterUrl: allChapterUrls) {
                Matcher m = p.matcher(chapterUrl);
                if(m.find()) {
                    allChapters.add(m.group());
                    allChapterMap.put(m.group(), chapterUrl);
                }
            }

            List<String> toBeDownloadedChapterNumbers = getYetToBeDownloadedChapterNumbers(allChapters, downloadedChapterNumbers);
            for(String toBeDownloadedChapterNumber: toBeDownloadedChapterNumbers) {
                MangaChapter mangaChapter = new MangaChapter();
                mangaChapter.setId(UUID.randomUUID().toString());
                mangaChapter.setChapterNumber(toBeDownloadedChapterNumber);
                mangaChapter.setChapterUrl(allChapterMap.get(toBeDownloadedChapterNumber));
                mangaChapter.setStatus(MangaChapterStatusEnum.ACTIVE);
                try {
                    mangaChapter = insertChapterForMangaSeries(mangaChapter, mangaSeries.getDatabaseName());
                    System.out.println(mangaChapter);
                } catch (DaoException daoex) {
                    System.out.println("DaoException!");
                }
            }

            List<MangaChapter> yetToBeDownloadedChapters = new ArrayList<>();
            try {
                yetToBeDownloadedChapters = findMangaChaptersToBeDownloaded(MangaChapterStatusEnum.ACTIVE, mangaSeries.getDatabaseName());
                System.out.println("YetToBeDownloaded:" + yetToBeDownloadedChapters);
            } catch (DaoException daoex) {
                System.out.println("DaoException!");
            }

            try {
                downloadMangaChapters(yetToBeDownloadedChapters, mangaSeries.getDatabaseName(), mangaSeries.getMangaName());
            } catch (IOException | DocumentException ioex) {
                ioex.printStackTrace();
            }

        }
    }

}
