package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentJobWorkerTest {

    @Test
    void multipleWorkersShouldProcessJobsConcurrently() throws InterruptedException {
        JobQueue queue = new JobQueue();
        Job job1 = new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        Job job2 = new Job("job-2", JobType.REPORT, JobPriority.MEDIUM);
        Job job3 = new Job("job-3", JobType.DATA_PROCESSING, JobPriority.LOW);
        queue.add(job1);
        queue.add(job2);
        queue.add(job3);
        CountDownLatch jobsCompleted = new CountDownLatch(3);
        JobWorker worker1 = createWorker(queue, jobsCompleted);
        JobWorker worker2 = createWorker(queue, jobsCompleted);
        JobWorker worker3 = createWorker(queue, jobsCompleted);
        Thread thread1 = new Thread(worker1);
        Thread thread2 = new Thread(worker2);
        Thread thread3 = new Thread(worker3);
        thread1.start();
        thread2.start();
        thread3.start();
        assertTrue(jobsCompleted.await(2, TimeUnit.SECONDS));
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
        thread1.join();
        thread2.join();
        thread3.join();
        assertEquals(JobStatus.COMPLETED, job1.getStatus());
        assertEquals(JobStatus.COMPLETED, job2.getStatus());
        assertEquals(JobStatus.COMPLETED, job3.getStatus());
    }

    private JobWorker createWorker(JobQueue queue,CountDownLatch jobsCompleted){
        return new JobWorker(queue){
            @Override 
            protected void execute(Job job) throws InterruptedException{
                Thread.sleep(100);
                jobsCompleted.countDown();
            }
        };
    }
}
