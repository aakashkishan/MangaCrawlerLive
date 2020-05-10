package com.manga.crawler.live.exceptions;

public class DaoException extends RuntimeException {

    public DaoException(String s) { super(s); }

    public DaoException(String s, Throwable t) { super(s, t); }

}
