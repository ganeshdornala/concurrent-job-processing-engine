package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;

public class EmailJobExecutor implements JobExecutor{
    
    @Override
    public String execute(Job job){
        return "Executed email job: "+job.getId();
    } 

}
