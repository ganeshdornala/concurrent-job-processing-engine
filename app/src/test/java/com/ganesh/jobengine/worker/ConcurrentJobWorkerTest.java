package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentJobWorkerTest {
    
    @Test 
    void multipleWorkersShouldProcessJobsConcurrently() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job1=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        Job job2=new Job("job-2", JobType.REPORT, JobPriority.MEDIUM);
        Job job3=new Job("job-3", JobType.DATA_PROCESSING, JobPriority.LOW);
        queue.add(job1);
        queue.add(job2);
        queue.add(job3);
        JobWorkerPool pool=new JobWorkerPool(3);
        pool.start(queue);
        Thread.sleep(500);
        pool.shutdown();
        assertEquals(JobStatus.COMPLETED, job1.getStatus());
        assertEquals(JobStatus.COMPLETED, job2.getStatus());
        assertEquals(JobStatus.COMPLETED, job3.getStatus());
    }

}
