package com.undefware.bookparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: wolong
 * Date: 3/4/13
 * Time: 10:33 AM
 */
public class BookParserExecutor {
    private static final Logger logger = LoggerFactory.getLogger(BookParserExecutor.class);

    private ThreadPoolExecutor threadPoolExecutor;


    public BookParserExecutor() {

    }


    public void start() {
        threadPoolExecutor =
                new ThreadPoolExecutor(8, 8, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10000));
    }

    public void stop() {
        threadPoolExecutor.shutdown();
    }

    public void pushBook(File file) {
        threadPoolExecutor.execute(new BookParser(file));
    }
}
