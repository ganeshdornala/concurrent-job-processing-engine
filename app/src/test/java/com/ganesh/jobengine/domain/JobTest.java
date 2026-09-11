package com.ganesh.jobengine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobTest {
    
    @Test 
    void jobShouldBeCreatedWithPendingStatus(){
        Job job=new Job(
            "job-1",
            JobType.EMAIL,
            JobPriority.HIGH
        );
        assertEquals("job-1", job.getId());
        assertEquals(JobType.EMAIL, job.getType());
        assertEquals(JobPriority.HIGH, job.getPriority());
        assertEquals(JobStatus.PENDING, job.getStatus());
    }

    @Test 
    void jobStatusShouldBeChangeable(){
        Job job=new Job(
            "job-2",
            JobType.REPORT,
            JobPriority.MEDIUM
        );
        job.setStatus(JobStatus.RUNNING);
        assertEquals(JobStatus.RUNNING, job.getStatus());
    }

    @Test 
    void jobShouldRejectNullId(){
        assertThrows(
            IllegalArgumentException.class,
            ()->new Job(null,JobType.EMAIL,JobPriority.HIGH)
        );
    }

    @Test 
    void jobShouldRejectBlankId(){
        assertThrows(
            IllegalArgumentException.class,
            ()->new Job("   ",JobType.EMAIL,JobPriority.HIGH)
        );
    }

    @Test 
    void jobShouldRejectNullType(){
        assertThrows(
            IllegalArgumentException.class,
            ()->new Job("job-3",null,JobPriority.HIGH)
        );
    }

    @Test 
    void jobShouldRejectNullPriority(){
        assertThrows(
            IllegalArgumentException.class,
            ()->new Job("job-4",JobType.EMAIL,null)
        );
    }

}
