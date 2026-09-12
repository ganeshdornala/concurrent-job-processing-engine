package com.ganesh.jobengine.queue;

import com.ganesh.jobengine.domain.Job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class JobQueue {
    
    private final BlockingQueue<Job> jobs;

    public JobQueue(){
        this.jobs=new PriorityBlockingQueue<>(11,new JobPriorityComparator());
    }

    public void add(Job job){
        jobs.add(job);
    }

    public Job poll(){
        return jobs.poll();
    }

    public Job take() throws InterruptedException{
        return jobs.take();
    }

    public int size(){
        return jobs.size();
    }

    public boolean isEmpty(){
        return jobs.isEmpty();
    }

}
