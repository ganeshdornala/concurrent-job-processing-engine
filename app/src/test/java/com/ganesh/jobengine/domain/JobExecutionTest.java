package com.ganesh.jobengine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobExecutionTest {

    @Test
    void shouldCreateJobExecutionRecord() {
        JobExecution execution = new JobExecution(1,JobStatus.FAILED,"Database connection failed");

        assertEquals(1, execution.getAttempt());
        assertEquals(JobStatus.FAILED, execution.getStatus());
        assertEquals("Database connection failed",execution.getMessage());
    }
}