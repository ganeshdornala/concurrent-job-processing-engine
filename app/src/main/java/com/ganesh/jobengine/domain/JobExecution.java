package com.ganesh.jobengine.domain;

public class JobExecution {

    private final int attempt;
    private final JobStatus status;
    private final String message;

    public JobExecution(int attempt,JobStatus status,String message) {
        this.attempt = attempt;
        this.status = status;
        this.message = message;
    }

    public int getAttempt() {
        return attempt;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}