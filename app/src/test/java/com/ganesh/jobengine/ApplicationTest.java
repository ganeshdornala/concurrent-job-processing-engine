package com.ganesh.jobengine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationTest {
    @Test
    void applicationMessageIsCorrect() {
        String message = "Concurrent Job Processing Engine";

        assertEquals(
                "Concurrent Job Processing Engine",
                message
        );
    }
}
