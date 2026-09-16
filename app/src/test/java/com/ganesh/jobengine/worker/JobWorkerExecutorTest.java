package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.queue.JobQueue;

import com.ganesh.jobengine.executor.JobExecutor;
import com.ganesh.jobengine.executor.JobExecutorRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerExecutorTest {

    @Test
    void workerShouldDelegateExecutionToRegisteredExecutor() throws InterruptedException {
        JobQueue queue = new JobQueue();
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        queue.add(job);
        JobExecutorRegistry registry = new JobExecutorRegistry();
        JobExecutor customExecutor =jobToExecute ->{
        }  ;
        registry.register(JobType.EMAIL, customExecutor);
        JobWorker worker = new JobWorker(queue, registry);
        worker.processNextJob();
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }
}