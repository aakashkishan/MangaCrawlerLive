package com.manga.crawler.live;

import com.manga.crawler.live.repository.impl.MangaDao;
import com.manga.crawler.live.service.MangaChapterService;
import com.manga.crawler.live.service.MangaSeriesService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException, ClassNotFoundException {
        // Run the Application
        MangaDao mangaDao = new MangaDao();
        MangaSeriesService mangaSeriesService = new MangaSeriesService(mangaDao);
        mangaSeriesService.setMangaSeriesForDownload("http://www.mangareader.net/bleach", "Bleach");

        TimeUnit.SECONDS.sleep(3);
        MangaChapterService mangaChapterService = new MangaChapterService(mangaDao);
        mangaChapterService.mangaChapterService();
    }
}
