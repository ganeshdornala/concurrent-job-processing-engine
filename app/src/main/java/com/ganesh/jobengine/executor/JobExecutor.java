package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;

@FunctionalInterface 
public interface JobExecutor {

    void execute(Job job);
    
}
