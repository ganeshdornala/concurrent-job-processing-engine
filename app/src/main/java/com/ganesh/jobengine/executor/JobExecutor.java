package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;

@FunctionalInterface 
public interface JobExecutor {

    String execute(Job job);
    
}
