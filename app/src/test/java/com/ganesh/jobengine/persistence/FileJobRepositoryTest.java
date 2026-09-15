package com.ganesh.jobengine.persistence;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobExecution;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileJobRepositoryTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldSaveAndLoadJob() throws IOException {
        FileJobRepository repository =new FileJobRepository(temporaryDirectory);
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        repository.save(job);
        Optional<Job> result =repository.findById("job-1");
        assertTrue(result.isPresent());
        Job restoredJob = result.get();
        assertEquals("job-1", restoredJob.getId());
        assertEquals(JobType.EMAIL, restoredJob.getType());
        assertEquals(JobPriority.HIGH, restoredJob.getPriority());
        assertEquals(JobStatus.PENDING, restoredJob.getStatus());
        assertEquals(0, restoredJob.getExecutionCount());
        assertEquals(job.getSequence(), restoredJob.getSequence());
    }

    @Test
    void shouldReturnEmptyWhenJobDoesNotExist() throws IOException {
        FileJobRepository repository =new FileJobRepository(temporaryDirectory);
        Optional<Job> result =repository.findById("missing-job");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPersistJobStateAndExecutionHistory() throws IOException {
        FileJobRepository repository =new FileJobRepository(temporaryDirectory);
        Job job = new Job("job-2",JobType.REPORT,JobPriority.MEDIUM);
        job.setStatus(JobStatus.FAILED);
        job.incrementExecutionCount();
        job.recordFailure("Report generation failed");
        job.addExecution(
            new JobExecution(1,JobStatus.FAILED,"Report generation failed")
        );
        repository.save(job);
        Job restoredJob =repository.findById("job-2").orElseThrow();
        assertEquals("job-2", restoredJob.getId());
        assertEquals(JobType.REPORT, restoredJob.getType());
        assertEquals(JobPriority.MEDIUM, restoredJob.getPriority());
        assertEquals(JobStatus.FAILED, restoredJob.getStatus());
        assertEquals(1, restoredJob.getExecutionCount());
        assertEquals("Report generation failed",restoredJob.getFailureMessage());
        assertEquals(1, restoredJob.getExecutionHistory().size());
        JobExecution execution =restoredJob.getExecutionHistory().get(0);
        assertEquals(1, execution.getAttempt());
        assertEquals(JobStatus.FAILED, execution.getStatus());
        assertEquals("Report generation failed",execution.getMessage());
    }

    @Test
    void shouldCreateStorageDirectoryWhenSavingJob() throws IOException {
        Path storageDirectory =temporaryDirectory.resolve("jobs");
        FileJobRepository repository =new FileJobRepository(storageDirectory);
        Job job = new Job("job-3",JobType.DATA_PROCESSING,JobPriority.LOW);
        repository.save(job);
        assertTrue(Files.exists(storageDirectory));
        assertTrue(Files.list(storageDirectory).findAny().isPresent());
    }
}