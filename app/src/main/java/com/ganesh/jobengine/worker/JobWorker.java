package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.queue.JobQueue;

import com.ganesh.jobengine.executor.JobExecutor;
import com.ganesh.jobengine.executor.JobExecutorRegistry;

public class JobWorker implements Runnable{
    
    private final JobQueue jobQueue;
    private final JobExecutorRegistry executorRegistry;

    public JobWorker(JobQueue jobQueue){
        this(jobQueue,new JobExecutorRegistry());
    }

    public JobWorker(JobQueue jobQueue,JobExecutorRegistry executorRegistry){
        this.jobQueue=jobQueue;
        this.executorRegistry=new JobExecutorRegistry();
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
            job.setStatus(JobStatus.COMPLETED);
        }catch(RuntimeException e){
            job.recordFailure(e.getMessage());
            job.setStatus(JobStatus.FAILED);
            if(job.canRetry()){
                job.setStatus(JobStatus.PENDING);
                jobQueue.add(job);
            }
        }
    }

    protected void execute(Job job) throws InterruptedException{
        JobExecutor executor=executorRegistry.getExecutor(job.getType());
        executor.execute(job);
    }

}
