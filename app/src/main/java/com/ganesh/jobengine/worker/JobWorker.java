package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.persistence.JobRepository;

import com.ganesh.jobengine.deadletter.DeadLetterQueue;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobExecution;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.queue.JobQueue;

import com.ganesh.jobengine.executor.JobExecutor;
import com.ganesh.jobengine.executor.JobExecutorRegistry;

import java.util.concurrent.Semaphore;

public class JobWorker implements Runnable{
    
    private final JobQueue jobQueue;
    private final JobExecutorRegistry executorRegistry;
    private final JobRepository jobRepository;
    private final DeadLetterQueue deadLetterQueue;
    private volatile boolean running=true;
    private final Semaphore resourceLimit;

    public JobWorker(JobQueue jobQueue) {
        this(jobQueue,new JobExecutorRegistry(),null,null,new Semaphore(1));
    }

    public JobWorker(JobQueue jobQueue,JobExecutorRegistry executorRegistry){
        this(jobQueue,executorRegistry,null,null,new Semaphore(1));
    }

    public JobWorker(JobQueue jobQueue,JobExecutorRegistry executorRegistry,JobRepository jobRepository){
        this(jobQueue,executorRegistry,jobRepository,null,new Semaphore(1));
    }

    public JobWorker(JobQueue jobQueue,JobExecutorRegistry executorRegistry,JobRepository jobRepository,DeadLetterQueue deadLetterQueue,Semaphore resourceLimit){
        this.jobQueue=jobQueue;
        this.executorRegistry=executorRegistry;
        this.jobRepository=jobRepository;
        this.deadLetterQueue=deadLetterQueue;
        this.resourceLimit=resourceLimit;
    }

    @Override 
    public void run(){
        while(running&&!Thread.currentThread().isInterrupted()){
            try{
                Job job=jobQueue.poll(100,java.util.concurrent.TimeUnit.MILLISECONDS);
                if(job!=null){
                    resourceLimit.acquire();
                    try{
                        processJob(job);
                    }finally{
                        resourceLimit.release();
                    }
                    
                }
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop(){
        running=false;
    }

    public void processNextJob() throws InterruptedException{
        Job job=jobQueue.take();
        processJob(job);
    }

    private void processJob(Job job) throws InterruptedException{
        job.setStatus(JobStatus.RUNNING);
        job.incrementExecutionCount();
        try{
            execute(job);
            job.addExecution(
                new JobExecution(
                    job.getExecutionCount(),
                    JobStatus.COMPLETED,
                    "Job completed successfully"
                )
            );
            job.setStatus(JobStatus.COMPLETED);
            saveJob(job);
        }catch(RuntimeException e){
            job.recordFailure(e.getMessage());
            job.addExecution(
                new JobExecution(
                    job.getExecutionCount(),
                    JobStatus.FAILED,
                    e.getMessage()
                )
            );
            job.setStatus(JobStatus.FAILED);
            if(job.canRetry()){
                job.setStatus(JobStatus.PENDING);
                saveJob(job);
                jobQueue.add(job);
            }else{
                moveToDeadLetterQueue(job);
                saveJob(job);
            }
        }
    }

    private void saveJob(Job job){
        if (jobRepository == null) {
            return;
        }
        try {
            jobRepository.save(job);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to persist job: " + job.getId(),e);
        }
    }

    private void moveToDeadLetterQueue(Job job){
        if(deadLetterQueue==null){
            return;
        }
        deadLetterQueue.add(job);
    }

    protected void execute(Job job) throws InterruptedException{
        JobExecutor executor=executorRegistry.getExecutor(job.getType());
        executor.execute(job);
    }

}
