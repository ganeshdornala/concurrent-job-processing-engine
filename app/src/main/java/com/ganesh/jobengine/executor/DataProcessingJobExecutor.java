package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;

public class DataProcessingJobExecutor implements JobExecutor{
    
    @Override 
    public String execute(Job job){
        return "Executed data processing job: "+job.getId();
    }

}
