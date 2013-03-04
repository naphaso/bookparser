package com.undefware.bookparser;

import java.io.File;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        BookParserExecutor bookParserExecutor = new BookParserExecutor();
        bookParserExecutor.start();
        bookParserExecutor.pushBook(new File(args[0]));
        bookParserExecutor.stop();
    }
}
