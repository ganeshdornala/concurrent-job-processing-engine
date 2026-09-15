package com.ganesh.jobengine.domain;

public class Job {
    
    private static final int MAX_ATTEMPTS=3;
    private final String id;
    private final JobType type;
    private final JobPriority priority;
    private JobStatus status;
    private int executionCount;
    private String failureMessage;
    private static long nextSequence=0;
    private final long sequence;

    public Job(String id, JobType type, JobPriority priority){
        if(id==null||id.isBlank()){
            throw new IllegalArgumentException("Job ID cannot be null or blank");
        }
        if(type==null){
            throw new IllegalArgumentException("Job type cannot be null");
        }
        if(priority==null){
            throw new IllegalArgumentException("Job priority cannot be null");
        }
        this.id=id;
        this.type=type;
        this.priority=priority;
        this.status=JobStatus.PENDING;
        this.executionCount=0;
        this.failureMessage=null;
        this.sequence=nextSequence++;
    }

    public String getId(){
        return id;
    }

    public JobType getType(){
        return type;
    }

    public JobPriority getPriority(){
        return priority;
    }

    public JobStatus getStatus(){
        return status;
    }

    public int getExecutionCount(){
        return executionCount;
    }

    public String getFailureMessage(){
        return failureMessage;
    }

    public long getSequence(){
        return sequence;
    }

    public void setStatus(JobStatus status){
        this.status=status;
    }

    public synchronized void incrementExecutionCount(){
        executionCount++;
    }

    public void recordFailure(String failureMessage){
        this.failureMessage=failureMessage;
    }

    public boolean canRetry(){
        return executionCount<MAX_ATTEMPTS;
    }

}
