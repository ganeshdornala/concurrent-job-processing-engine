package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobExecution;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerExecutionHistoryTest {

    @Test
    void successfulExecutionShouldBeRecordedInHistory() throws InterruptedException {
        JobQueue queue = new JobQueue();
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        queue.add(job);
        JobWorker worker = new JobWorker(queue);
        worker.processNextJob();
        assertEquals(1, job.getExecutionHistory().size());
        JobExecution execution =job.getExecutionHistory().get(0);
        assertEquals(1, execution.getAttempt());
        assertEquals(JobStatus.COMPLETED,execution.getStatus());
        assertEquals("Job completed successfully",execution.getMessage());
    }
}