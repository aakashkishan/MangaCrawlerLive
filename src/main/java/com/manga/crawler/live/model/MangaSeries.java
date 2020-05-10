package com.manga.crawler.live.model;

import com.manga.crawler.live.utils.MangaSeriesStatusEnum;
import lombok.Data;

@Data
public class MangaSeries {

    private String Id;

    private String mangaUrl;

    private String mangaName;

    private MangaSeriesStatusEnum status;

    private String databaseName;

}
