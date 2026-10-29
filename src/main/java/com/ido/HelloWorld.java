package com.ido;

/**
 * Hello world!
 */
public class HelloWorld {
    static final String NAME = "Yarin";

    static String message() {
        return "Hello World from " + NAME + "!";
    }

    public static void main(String[] args) {
        System.out.println(message());
    }
}
