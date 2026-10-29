package com.ido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void captureStdout() {
        originalOut = System.out;
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    @Test
    void messageIsHelloWorldWithMyName() {
        assertEquals("Hello World from Yarin!", HelloWorld.message());
    }

    @Test
    void messageContainsMyName() {
        assertTrue(HelloWorld.message().contains(HelloWorld.NAME));
    }

    @Test
    void mainPrintsTheMessageOnOneLine() {
        HelloWorld.main(new String[0]);

        assertEquals(HelloWorld.message() + System.lineSeparator(), captured.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainIgnoresArguments() {
        HelloWorld.main(new String[] {"unexpected", "args"});

        assertEquals(HelloWorld.message() + System.lineSeparator(), captured.toString(StandardCharsets.UTF_8));
    }
}
