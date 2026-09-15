package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.JobType;

import java.util.EnumMap;
import java.util.Map;

public class JobExecutorRegistry {
    
    private final Map<JobType,JobExecutor> executors;

    public JobExecutorRegistry(){
        executors=new EnumMap<>(JobType.class);
        executors.put(JobType.EMAIL,new EmailJobExecutor());
        executors.put(JobType.REPORT,new ReportJobExecutor());
        executors.put(JobType.DATA_PROCESSING,new DataProcessingJobExecutor());
    }

    public JobExecutor getExecutor(JobType jobType){
        JobExecutor executor=executors.get(jobType);
        if(executor==null){
            throw new IllegalArgumentException(
                "No executor registered for job type: "+jobType
            );
        }
        return executor;
    }

    public void register(JobType jobType, JobExecutor executor) {
        executors.put(jobType, executor);
    }

}
