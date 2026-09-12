package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerThreadTest {
    
    @Test 
    void workerShouldProcessJobInsideThread() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        queue.add(job);
        JobWorker worker=new JobWorker(queue);
        Thread workerThread=new Thread(worker);
        workerThread.start();
        Thread.sleep(500);
        workerThread.interrupt();
        workerThread.join();
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

}
