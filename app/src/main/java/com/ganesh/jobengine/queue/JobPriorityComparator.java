package com.ganesh.jobengine.queue;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;

import java.util.Comparator;

public class JobPriorityComparator implements Comparator<Job>{
    
    @Override 
    public int compare(Job first,Job second){
        return Integer.compare(
            priorityValue(second.getPriority()),
            priorityValue(first.getPriority())
        );
    }

    private int priorityValue(JobPriority priority){
        return switch(priority){
            case HIGH->3;
            case MEDIUM->2;
            case LOW->1;
        };
    }

}
