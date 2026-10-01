package com.ganesh.jobengine.worker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ganesh.jobengine.executor.JobExecutor;
import com.ganesh.jobengine.executor.JobExecutorRegistry;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JobWorkerPoolTest {

    @Test 
    void workerPoolShouldProcessMultipleJobs() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job1=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        Job job2=new Job("job-2", JobType.REPORT, JobPriority.MEDIUM);
        Job job3=new Job("job-3", JobType.DATA_PROCESSING, JobPriority.LOW);
        queue.add(job1);
        queue.add(job2);
        queue.add(job3);
        CountDownLatch completedJobs=new CountDownLatch(3);
        JobExecutorRegistry registry=new JobExecutorRegistry();
        JobExecutor testExecutor=job->completedJobs.countDown();
        registry.register(JobType.EMAIL, testExecutor);
        registry.register(JobType.REPORT, testExecutor);
        registry.register(JobType.DATA_PROCESSING, testExecutor);
        JobWorkerPool pool=new JobWorkerPool(3,null,null,registry);
        pool.start(queue);
        boolean completed=completedJobs.await(2, TimeUnit.SECONDS);
        pool.shutdown();
        assertTrue(completed);
        assertEquals(JobStatus.COMPLETED, job1.getStatus());
        assertEquals(JobStatus.COMPLETED, job2.getStatus());
        assertEquals(JobStatus.COMPLETED, job3.getStatus());
    }

    @Test 
    void workerPoolShouldRejectInvalidWorkerCount(){
        assertThrows(
            IllegalArgumentException.class,
            ()->new JobWorkerPool(0)
        );
    }

    @Test
    void shutdownShouldAllowRunningJobToFinish() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        queue.add(job);
        CountDownLatch jobStarted=new CountDownLatch(1);
        CountDownLatch allowJobToFinish=new CountDownLatch(1);
        AtomicBoolean interrupted=new AtomicBoolean(false);
        JobExecutorRegistry registry=new JobExecutorRegistry();
        JobExecutor blockingExecutor=currentJob->{
            jobStarted.countDown();
            try{
                allowJobToFinish.await();
            }catch(InterruptedException e){
                interrupted.set(true);
                Thread.currentThread().interrupt();
                return;
            }
        };
        registry.register(JobType.EMAIL, blockingExecutor);
        JobWorkerPool pool=new JobWorkerPool(1,null,null,registry);
        pool.start(queue);
        assertTrue(jobStarted.await(2,TimeUnit.SECONDS));
        Thread shutdownThread=new Thread(()->{
            try{
                pool.shutdown();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
        shutdownThread.start();
        Thread.sleep(100);
        assertTrue(shutdownThread.isAlive());
        allowJobToFinish.countDown();
        shutdownThread.join(2000);
        assertFalse(interrupted.get());
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }
    
}
