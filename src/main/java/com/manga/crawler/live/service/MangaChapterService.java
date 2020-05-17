package com.manga.crawler.live.service;

import com.itextpdf.text.DocumentException;
import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.model.MangaChapter;
import com.manga.crawler.live.model.MangaSeries;
import com.manga.crawler.live.repository.IMangaDao;
import com.manga.crawler.live.utils.*;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jsoup.Jsoup.connect;

public class MangaChapterService extends TimerTask {

    private IMangaDao mangaDao;

    public MangaChapterService(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
        new DaoUtils(this.mangaDao);
    }

    @SneakyThrows
    @Override
    public void run() {
        mangaChapterService();
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
                DaoUtils.updateMangaChapterStatus(databaseName, MangaChapterStatusEnum.DISABLED, mangaChapter.getId());
            } catch (DaoException daoex) {
                System.out.println("DaoException!");
            }
        }

        // Set the Status of the Manga Series to DISABLED once all chapters are downloaded
        try {
            DaoUtils.updateMangaSeriesStatusByMangaName(MangaSeriesStatusEnum.DISABLED, mangaName);
        } catch (DaoException daoex) {
            System.out.println("DaoException!");
        }
    }

    public void mangaChapterService() throws IOException, DaoException {

        System.out.println("Manga Chapter Service Starting!");
        List<MangaSeries> listOfMangas = null;
        try {
            listOfMangas = DaoUtils.findMangaSeriesByStatus(MangaSeriesStatusEnum.ACTIVE);
        } catch (DaoException daoex) {
            System.out.println("DaoException!");
        }

        for(MangaSeries mangaSeries: listOfMangas) {
            // Get the already downloaded chapters for the manga-series
            List<String> downloadedChapterNumbers = new ArrayList<>();
            try {
                downloadedChapterNumbers = DaoUtils.findTheDownloadedMangaChaptersOfMangaSeries(mangaSeries.getDatabaseName());
            } catch (DaoException daoex) {
                System.out.println("DaoException!");
            }

            // Get all chapters for the manga series
            List<String> allChapterUrls = DaoUtils.getAllChaptersOfMangaSeries(mangaSeries);
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

            List<String> toBeDownloadedChapterNumbers = DaoUtils.getYetToBeDownloadedChapterNumbers(allChapters, downloadedChapterNumbers);
            for(String toBeDownloadedChapterNumber: toBeDownloadedChapterNumbers) {
                MangaChapter mangaChapter = new MangaChapter();
                mangaChapter.setId(UUID.randomUUID().toString());
                mangaChapter.setChapterNumber(toBeDownloadedChapterNumber);
                mangaChapter.setChapterUrl(allChapterMap.get(toBeDownloadedChapterNumber));
                mangaChapter.setStatus(MangaChapterStatusEnum.ACTIVE);
                try {
                    mangaChapter = DaoUtils.insertChapterForMangaSeries(mangaChapter, mangaSeries.getDatabaseName());
                    System.out.println(mangaChapter);
                } catch (DaoException daoex) {
                    System.out.println("DaoException!");
                }
            }

            List<MangaChapter> yetToBeDownloadedChapters = new ArrayList<>();
            try {
                yetToBeDownloadedChapters = DaoUtils.findMangaChaptersToBeDownloaded(MangaChapterStatusEnum.ACTIVE, mangaSeries.getDatabaseName());
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
