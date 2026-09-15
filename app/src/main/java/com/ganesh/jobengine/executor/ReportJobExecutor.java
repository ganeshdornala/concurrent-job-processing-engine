package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;

public class ReportJobExecutor implements JobExecutor{
    
    @Override 
    public String execute(Job job){
        return "Executed report job: "+job.getId();
    }

}
