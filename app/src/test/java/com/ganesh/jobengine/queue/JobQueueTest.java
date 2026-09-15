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
    void queueShouldReturnJobsInPriorityOrder(){
        JobQueue queue=new JobQueue();
        Job lowPriorityJob=new Job("job-low", JobType.EMAIL, JobPriority.LOW);
        Job highPriorityJob=new Job("job-high", JobType.REPORT, JobPriority.HIGH);
        Job mediumPriorityJob=new Job("job-medium", JobType.DATA_PROCESSING, JobPriority.MEDIUM);
        queue.add(lowPriorityJob);
        queue.add(highPriorityJob);
        queue.add(mediumPriorityJob);
        assertEquals(highPriorityJob, queue.poll());
        assertEquals(mediumPriorityJob, queue.poll());
        assertEquals(lowPriorityJob, queue.poll());
    }
    
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

    @Test 
    void queueShouldPreserveInsertionOrderForSamePriority(){
        JobQueue queue=new JobQueue();
        Job firstJob=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        Job secondJob=new Job("job-2",JobType.REPORT,JobPriority.HIGH);
        Job thirdJob=new Job("job-3",JobType.DATA_PROCESSING,JobPriority.HIGH);
        queue.add(firstJob);
        queue.add(secondJob);
        queue.add(thirdJob);
        assertEquals(firstJob, queue.poll());
        assertEquals(secondJob, queue.poll());
        assertEquals(thirdJob, queue.poll());
    }

}
