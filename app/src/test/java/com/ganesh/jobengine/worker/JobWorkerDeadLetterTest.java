package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.deadletter.DeadLetterQueue;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;

import com.ganesh.jobengine.executor.JobExecutorRegistry;

import com.ganesh.jobengine.queue.JobQueue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Semaphore;

import javax.management.RuntimeErrorException;

class JobWorkerDeadLetterTest {
    
    @Test 
    void shouldMoveJobToDeadLetterQueueAfterMaximumAttempts() throws InterruptedException{
        JobQueue jobQueue=new JobQueue();
        DeadLetterQueue deadLetterQueue=new DeadLetterQueue();
        Job job=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        jobQueue.add(job);
        JobWorker worker=new JobWorker(jobQueue,new JobExecutorRegistry(),null,deadLetterQueue,new Semaphore(1)){
            @Override 
            protected void execute(Job job) throws InterruptedException{
                throw new RuntimeException("Email service unavailable");
            }
        };
        worker.processNextJob();
        worker.processNextJob();
        worker.processNextJob();
        assertEquals(1, deadLetterQueue.size());
        Job deadLetterJob=deadLetterQueue.poll();
        assertEquals(job, deadLetterJob);
        assertEquals(3, deadLetterJob.getExecutionCount());
    }

}
