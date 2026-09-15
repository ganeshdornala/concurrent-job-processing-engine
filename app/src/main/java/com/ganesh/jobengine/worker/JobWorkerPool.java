package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.queue.JobQueue;

import com.ganesh.jobengine.persistence.JobRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JobWorkerPool {
    
    private final ExecutorService executorService;
    private final int workerCount;
    private final JobRepository jobRepository;

    public JobWorkerPool(int workerCount){
        this(workerCount,null);
    }
    
    public JobWorkerPool(int workerCount,JobRepository jobRepository){
        if(workerCount<=0){
            throw new IllegalArgumentException(
                "Worker count must be greater than zero"
            );
        }
        this.workerCount=workerCount;
        this.jobRepository=jobRepository;
        this.executorService=Executors.newFixedThreadPool(workerCount);
    }

    public void start(JobQueue jobQueue){
        for(int i=0;i<workerCount;i++){
            executorService.submit(new JobWorker(
                jobQueue,
                new com.ganesh.jobengine.executor.JobExecutorRegistry(),
                jobRepository
            ));
        }
    }

    public void shutdown() throws InterruptedException{
        executorService.shutdownNow();
        if(!executorService.awaitTermination(5, TimeUnit.SECONDS)){
            throw new IllegalStateException(
                "Worker pool did not terminate within the timeout"
            );
        }
    }

}
