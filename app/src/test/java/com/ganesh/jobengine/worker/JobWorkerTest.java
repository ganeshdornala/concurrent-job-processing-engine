package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerTest {
    
    @Test
    void workerShouldCompletePendingJob() throws InterruptedException{
        JobQueue queue = new JobQueue();
        Job job = new Job(
                "job-1",
                JobType.EMAIL,
                JobPriority.HIGH
        );
        queue.add(job);
        JobWorker worker = new JobWorker(queue);
        assertEquals(JobStatus.PENDING, job.getStatus());
        worker.processNextJob();
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

    @Test
    void workerShouldProcessJobWhenQueueContainsJob() throws InterruptedException{
        JobQueue queue = new JobQueue();
        Job job = new Job(
                "job-2",
                JobType.REPORT,
                JobPriority.MEDIUM
        );
        queue.add(job);
        JobWorker worker = new JobWorker(queue);
        worker.processNextJob();
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

}
