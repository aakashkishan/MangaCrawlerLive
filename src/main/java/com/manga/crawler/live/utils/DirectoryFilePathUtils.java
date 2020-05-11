package com.manga.crawler.live.utils;

public class DirectoryFilePathUtils {

    public static String generateMangaSeriesDirectoryPath(String mangaName) {
        return String.format("./%s", mangaName);
    }

    public static String generateMangaChapterDirectoryPathForMangaSeries(String chapterNumber, String mangaName) {
        return String.format("./%s/Chapter %s", mangaName, chapterNumber);
    }

    public static String generatePageFilePathForMangaChapterInMangaSeries(String pageNumber, String chapterNumber, String mangaName) {
        return String.format("./%s/Chapter %s/Page %s.png", mangaName, chapterNumber, pageNumber);
    }

    public static String generatePDFPathForMangaChapterInMangaSeries(String chapterNumber, String mangaName) {
        return String.format("./%s/Chapter %s/Chapter %s.pdf", mangaName, chapterNumber);
    }

}
