package com.ganesh.jobengine.deadletter;

import com.ganesh.jobengine.domain.Job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DeadLetterQueue {
    
    private final BlockingQueue<Job> jobs;

    public DeadLetterQueue(){
        this.jobs=new LinkedBlockingQueue<>();
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
