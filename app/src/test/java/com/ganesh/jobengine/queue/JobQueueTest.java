package com.ganesh.jobengine.queue;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobQueueTest {
    
    @Test 
    void jobsShouldBeRetrievedInPriorityOrder(){
        JobQueue queue=new JobQueue();
        Job low=new Job("job-low", JobType.EMAIL, JobPriority.LOW);
        Job high=new Job("job-high", JobType.EMAIL, JobPriority.HIGH);
        Job medium=new Job("job-medium", JobType.EMAIL, JobPriority.MEDIUM);
        queue.add(low);
        queue.add(high);
        queue.add(medium);
        assertEquals("job-high", queue.poll().getId());
        assertEquals("job-medium", queue.poll().getId());
        assertEquals("job-low", queue.poll().getId());
    }

    @Test
    void queueShouldReportCorrectSize() {
        JobQueue queue = new JobQueue();
        queue.add(new Job("job-1", JobType.EMAIL, JobPriority.LOW));
        queue.add(new Job("job-2", JobType.REPORT, JobPriority.HIGH));
        assertEquals(2, queue.size());
        queue.poll();
        assertEquals(1, queue.size());
    }

    @Test
    void emptyQueueShouldReturnNullWhenPolled() {
        JobQueue queue = new JobQueue();
        assertTrue(queue.isEmpty());
        assertEquals(null, queue.poll());
    }

    @Test
    void newlyAddedJobsShouldStartAsPending() {
        JobQueue queue = new JobQueue();
        queue.add(new Job("job-1", JobType.DATA_PROCESSING, JobPriority.HIGH));
        assertEquals(JobStatus.PENDING, queue.poll().getStatus());
    }

}
