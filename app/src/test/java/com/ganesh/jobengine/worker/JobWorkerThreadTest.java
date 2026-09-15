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

class JobWorkerThreadTest {
    
    @Test 
    void workerShouldProcessJobInsideThread() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        queue.add(job);
        JobWorker worker=new JobWorker(queue);
        CountDownLatch jobCompleted=new CountDownLatch(1);
        Thread workerThread=new Thread(()->{
            try{
                worker.processNextJob();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }finally{
                jobCompleted.countDown();
            }
        });
        workerThread.start();
        assertTrue(jobCompleted.await(2,TimeUnit.SECONDS));
        workerThread.join();
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

}
