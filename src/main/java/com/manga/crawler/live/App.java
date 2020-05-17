package com.manga.crawler.live;

import com.manga.crawler.live.repository.impl.MangaDao;
import com.manga.crawler.live.service.MangaChapterService;
import com.manga.crawler.live.service.MangaCli;
import com.manga.crawler.live.service.MangaSeriesService;
import com.manga.crawler.live.service.ReactivateLaggingMangaSeries;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Timer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        // Run the Application
        MangaDao mangaDao = new MangaDao();
        ReactivateLaggingMangaSeries reactivateLaggingMangaSeries = new ReactivateLaggingMangaSeries(mangaDao);
        MangaChapterService mangaChapterService = new MangaChapterService(mangaDao);

        Timer timer = new Timer();
        new CommandLine(new MangaCli(mangaDao)).execute(args);
        timer.scheduleAtFixedRate(mangaChapterService, 5 * 1000, 3600 * 1000);
        timer.scheduleAtFixedRate(reactivateLaggingMangaSeries, 60 * 1000, 3600 * 1000);

    }
}
