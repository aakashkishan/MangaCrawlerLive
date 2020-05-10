package com.manga.crawler.live.exceptions;

public class ParamValidationException extends RuntimeException {

    public ParamValidationException(String s) { super(s); }

    public ParamValidationException(String s, Throwable t) { super(s, t); }

}
