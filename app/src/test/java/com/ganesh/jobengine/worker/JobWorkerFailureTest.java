package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerFailureTest {
    
    @Test 
    void workerShouldMarkJobAsFailedWhenExecutionThrowsException() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job=new Job("job-failure", JobType.DATA_PROCESSING, JobPriority.HIGH);
        queue.add(job);
        JobWorker worker=new JobWorker(queue){
            @Override 
            protected void execute(Job job){
                throw new RuntimeException("Job execution failed");
            }
        };
        worker.processNextJob();
        assertEquals(JobStatus.FAILED, job.getStatus());
    }

    @Test 
    void failedJobShouldStillIncreaseExecutionCount() throws InterruptedException{
        JobQueue queue=new JobQueue();
        Job job=new Job("job-failure-count", JobType.DATA_PROCESSING, JobPriority.HIGH);
        queue.add(job);
        JobWorker worker=new JobWorker(queue){
            @Override 
            protected void execute(Job job){
                throw new RuntimeException("Job execution failed");
            }
        };
        worker.processNextJob();
        assertEquals(1, job.getExecutionCount());
    }

}
