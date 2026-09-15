package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.persistence.FileJobRepository;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerPoolPersistenceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldPersistJobProcessedByWorkerPool() throws IOException, InterruptedException {
        FileJobRepository repository =new FileJobRepository(temporaryDirectory);
        JobQueue queue = new JobQueue();
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        queue.add(job);
        JobWorkerPool workerPool =new JobWorkerPool(1, repository);
        workerPool.start(queue);
        long timeout = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < timeout) {
            if (job.getStatus() == JobStatus.COMPLETED) {
                break;
            }
            Thread.sleep(10);
        }
        workerPool.shutdown();
        Job restoredJob =repository.findById("job-1").orElseThrow();
        assertEquals(JobStatus.COMPLETED,restoredJob.getStatus());
        assertEquals(1,restoredJob.getExecutionCount());
        assertEquals(1,restoredJob.getExecutionHistory().size());
    }
}