package com.manga.crawler.live.service;

import com.manga.crawler.live.repository.IMangaDao;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

public class MangaCli implements Callable<Integer> {

    private IMangaDao mangaDao;

    public MangaCli(IMangaDao mangaDao) {
        this.mangaDao = mangaDao;
    }

    @Parameters(index = "0", description = "Jar File")
    private File jarFile;

    @Parameters(index = "1", description = "MangaName")
    private String mangaName;

    @Parameters(index = "2", description = "MangaUrl")
    private String mangaUrl;

    @Override
    public Integer call() {
        new MangaSeriesService(mangaDao).setMangaSeriesForDownload(mangaUrl, mangaName);
        return 0;
    }

}
