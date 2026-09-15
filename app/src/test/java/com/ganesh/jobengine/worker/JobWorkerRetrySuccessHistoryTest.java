package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobExecution;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerRetrySuccessHistoryTest {

    @Test
    void failedAttemptFollowedBySuccessfulAttemptShouldBeRecorded() throws InterruptedException {
        JobQueue queue = new JobQueue();
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        queue.add(job);
        JobWorker worker = new JobWorker(queue) {
            @Override
            protected void execute(Job job) {
                if (job.getExecutionCount() == 1) {
                    throw new RuntimeException("Email service temporarily unavailable");
                }
            }
        };
        worker.processNextJob();
        worker.processNextJob();
        assertEquals(2, job.getExecutionHistory().size());
        JobExecution firstExecution =job.getExecutionHistory().get(0);
        JobExecution secondExecution =job.getExecutionHistory().get(1);
        assertEquals(1, firstExecution.getAttempt());
        assertEquals(JobStatus.FAILED,firstExecution.getStatus());
        assertEquals("Email service temporarily unavailable",firstExecution.getMessage());
        assertEquals(2, secondExecution.getAttempt());
        assertEquals(JobStatus.COMPLETED,secondExecution.getStatus());
        assertEquals("Job completed successfully",secondExecution.getMessage());
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }
}