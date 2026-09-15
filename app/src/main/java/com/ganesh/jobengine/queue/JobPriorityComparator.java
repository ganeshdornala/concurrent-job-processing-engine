package com.ganesh.jobengine.queue;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;

import java.util.Comparator;

public class JobPriorityComparator implements Comparator<Job>{
    
    @Override 
    public int compare(Job first,Job second){
        int priorityComparison= Integer.compare(
            priorityValue(second.getPriority()),
            priorityValue(first.getPriority())
        );
        if(priorityComparison!=0){
            return priorityComparison;
        }
        return Long.compare(first.getSequence(), second.getSequence());
    }

    private int priorityValue(JobPriority priority){
        return switch(priority){
            case HIGH->3;
            case MEDIUM->2;
            case LOW->1;
        };
    }

}
