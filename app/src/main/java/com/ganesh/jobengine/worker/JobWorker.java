package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.queue.JobQueue;

public class JobWorker implements Runnable{
    
    private final JobQueue jobQueue;

    public JobWorker(JobQueue jobQueue){
        this.jobQueue=jobQueue;
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
            job.setStatus(JobStatus.FAILED);
        }
    }

    protected  void execute(Job job) throws InterruptedException{
        System.out.println("Processing job: "+job.getId());
        Thread.sleep(100);
    }

}
