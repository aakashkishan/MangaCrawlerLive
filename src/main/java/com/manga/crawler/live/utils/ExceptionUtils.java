package com.manga.crawler.live.utils;

import com.manga.crawler.live.exceptions.DaoException;
import com.manga.crawler.live.exceptions.ParamValidationException;
import org.apache.commons.lang3.StringUtils;

public class ExceptionUtils {

    public final static String DAO_EXCEPTION_MESSAGE = "could not perform the SQL transaction!";

    public final static String DAO_EXCEPTION_MANGA_SERIES_BY_ID = "could not perform the SQL transaction for manga_series={%s}";

    public final static String DAO_EXCEPTION_MANGA_CHAPTER_BY_ID = "could not perform the SQL transaction for manga_chapter={%s}";

    public final static String PARAM_VALIDATION_MESSAGE = "Input parameter is null/blank!";

    public final static String PARAM_VALIDATION_PATTERN = "Inout parameter={%s} is null!";

    public static void throwDaoExceptionForMangaSeriesById(String id, Exception ex) {
        throw new DaoException(String.format(DAO_EXCEPTION_MANGA_SERIES_BY_ID, id), ex);
    }

    public static void throwDaoExceptionForMangaChapterById(String id, Exception ex) {
        throw new DaoException(String.format(DAO_EXCEPTION_MANGA_CHAPTER_BY_ID, id), ex);
    }

    public static void throwParamValidationExceptionIfNull(String paramName, Object param) {
        if(param == null) {
            throw new ParamValidationException(String.format(PARAM_VALIDATION_PATTERN, paramName));
        }
    }

    public static void throwParamValidationExceptionIfBlank(String paramName, String param) {
        if(StringUtils.isBlank(param)) {
            throw new ParamValidationException(String.format(PARAM_VALIDATION_PATTERN, paramName));
        }
    }

}
