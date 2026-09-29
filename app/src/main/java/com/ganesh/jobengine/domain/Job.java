package com.ganesh.jobengine.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Job {
    
    private static final int MAX_ATTEMPTS=3;
    private final String id;
    private final JobType type;
    private final JobPriority priority;
    private JobStatus status;
    private int executionCount;
    private String failureMessage;
    private static final AtomicLong NEXT_SEQUENCE = new AtomicLong();
    private final long sequence;
    private final List<JobExecution> executionHistory;

    public Job(String id, JobType type, JobPriority priority) {
        this(id,type,priority,JobStatus.PENDING,0,null,NEXT_SEQUENCE.getAndIncrement());
    }

    private Job(String id,JobType type,JobPriority priority,JobStatus status,int executionCount,String failureMessage,long sequence){
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Job type cannot be null");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Job priority cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Job status cannot be null");
        }
        if (executionCount < 0) {
            throw new IllegalArgumentException("Execution count cannot be negative");
        }
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.executionCount = executionCount;
        this.failureMessage = failureMessage;
        this.sequence = sequence;
        this.executionHistory = new ArrayList<>();
    }

    public static Job restore(String id,JobType type,JobPriority priority,JobStatus status,int executionCount,String failureMessage,long sequence){
        Job job = new Job(id,type,priority,status,executionCount,failureMessage,sequence);
        if (sequence != Long.MAX_VALUE) {
            NEXT_SEQUENCE.updateAndGet(current -> Math.max(current, sequence + 1));
        }
        return job;
    }

    public void restoreExecution(JobExecution execution) {
        executionHistory.add(execution);
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

    public List<JobExecution> getExecutionHistory() {
        return List.copyOf(executionHistory);
    }

    public void setStatus(JobStatus status){
        if(status==null){
            throw new IllegalArgumentException("Job status cannot be null");
        }
        this.status=status;
    }

    public synchronized void incrementExecutionCount(){
        executionCount++;
    }

    public void recordFailure(String failureMessage){
        this.failureMessage=failureMessage;
    }

    public void addExecution(JobExecution execution) {
        executionHistory.add(execution);    
    }

    public boolean canRetry(){
        return executionCount<MAX_ATTEMPTS;
    }

}
