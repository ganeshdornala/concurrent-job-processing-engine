package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.persistence.JobRepository;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobExecution;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.queue.JobQueue;

import com.ganesh.jobengine.executor.JobExecutor;
import com.ganesh.jobengine.executor.JobExecutorRegistry;

public class JobWorker implements Runnable{
    
    private final JobQueue jobQueue;
    private final JobExecutorRegistry executorRegistry;
    private final JobRepository jobRepository;

    public JobWorker(JobQueue jobQueue) {
        this(jobQueue,new JobExecutorRegistry(),null);
    }

    public JobWorker(JobQueue jobQueue,JobExecutorRegistry executorRegistry){
        this(jobQueue,executorRegistry,null);
    }

    public JobWorker(JobQueue jobQueue,JobExecutorRegistry executorRegistry,JobRepository jobRepository){
        this.jobQueue=jobQueue;
        this.executorRegistry=executorRegistry;
        this.jobRepository=jobRepository;
    }

    @Override 
    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            try{
                processNextJob();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void processNextJob() throws InterruptedException{
        Job job=jobQueue.take();
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
                jobQueue.add(job);
            }
            saveJob(job);
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

    protected void execute(Job job) throws InterruptedException{
        JobExecutor executor=executorRegistry.getExecutor(job.getType());
        executor.execute(job);
    }

}
