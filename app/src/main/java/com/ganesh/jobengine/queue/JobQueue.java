package com.ganesh.jobengine.queue;

import com.ganesh.jobengine.domain.Job;

import java.util.PriorityQueue;
import java.util.Queue;

public class JobQueue {
    
    private final Queue<Job> jobs;

    public JobQueue(){
        this.jobs=new PriorityQueue<>(new JobPriorityComparator());
    }

    public void add(Job job){
        jobs.add(job);
    }

    public Job poll(){
        return jobs.poll();
    }

    public int size(){
        return jobs.size();
    }

    public boolean isEmpty(){
        return jobs.isEmpty();
    }

}
