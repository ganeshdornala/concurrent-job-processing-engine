package com.ganesh.jobengine.persistence;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobExecution;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

public class FileJobRepository implements JobRepository {

    private final Path storageDirectory;

    public FileJobRepository(Path storageDirectory) {
        if (storageDirectory == null) {
            throw new IllegalArgumentException("Storage directory cannot be null");
        }
        this.storageDirectory = storageDirectory;
    }

    @Override
    public void save(Job job) throws IOException {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null");
        }
        Files.createDirectories(storageDirectory);
        Properties properties = new Properties();
        properties.setProperty("id", job.getId());
        properties.setProperty("type", job.getType().name());
        properties.setProperty("priority", job.getPriority().name());
        properties.setProperty("status", job.getStatus().name());
        properties.setProperty("executionCount",String.valueOf(job.getExecutionCount()));
        properties.setProperty("sequence",String.valueOf(job.getSequence()));
        if (job.getFailureMessage() != null) {
            properties.setProperty("failureMessage",job.getFailureMessage());
        }
        properties.setProperty("executionHistory.size",String.valueOf(job.getExecutionHistory().size()));
        for (int i = 0; i < job.getExecutionHistory().size(); i++) {
            JobExecution execution = job.getExecutionHistory().get(i);
            properties.setProperty("execution." + i + ".attempt",String.valueOf(execution.getAttempt()));
            properties.setProperty("execution." + i + ".status",execution.getStatus().name());
            if (execution.getMessage() != null) {
                properties.setProperty("execution." + i + ".message",execution.getMessage());
            }
        }
        Path jobFile = getJobFile(job.getId());
        try (Writer writer = Files.newBufferedWriter(jobFile,StandardCharsets.UTF_8)){
            properties.store(writer,"Concurrent Job Processing Engine");
        }
    }

    @Override
    public Optional<Job> findById(String jobId) throws IOException {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null or blank");
        }
        Path jobFile = getJobFile(jobId);
        if (!Files.exists(jobFile)) {
            return Optional.empty();
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(jobFile,StandardCharsets.UTF_8)){
            properties.load(reader);
        }
        Job job = Job.restore(
            properties.getProperty("id"),
            JobType.valueOf(properties.getProperty("type")),
            JobPriority.valueOf(properties.getProperty("priority")),
            JobStatus.valueOf(properties.getProperty("status")),
            Integer.parseInt(properties.getProperty("executionCount")),
            properties.getProperty("failureMessage"),
            Long.parseLong(properties.getProperty("sequence"))
        );

        int historySize = Integer.parseInt(
                properties.getProperty("executionHistory.size","0")
        );

        for (int i = 0; i < historySize; i++) {
            JobExecution execution = new JobExecution(
                    Integer.parseInt(properties.getProperty("execution." + i + ".attempt")),
                    JobStatus.valueOf(properties.getProperty("execution." + i + ".status")),
                    properties.getProperty("execution." + i + ".message")
            );
            job.restoreExecution(execution);
        }
        return Optional.of(job);
    }

    private Path getJobFile(String jobId) {
        String encodedId = Base64.getUrlEncoder().withoutPadding().encodeToString(jobId.getBytes(StandardCharsets.UTF_8));
        return storageDirectory.resolve(encodedId + ".properties");
    }
}