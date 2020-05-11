package com.manga.crawler.live.model;

import com.manga.crawler.live.utils.MangaChapterStatusEnum;
import lombok.Data;

@Data
public class MangaChapter {

    private String id;

    private String chapterNumber;

    private String chapterUrl;

    private MangaChapterStatusEnum status;

}
