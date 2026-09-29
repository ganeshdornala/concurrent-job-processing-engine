package com.ganesh.jobengine.deadletter;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeadLetterQueueTest {

    @Test
    void shouldAddAndPollJob(){
        DeadLetterQueue deadLetterQueue=new DeadLetterQueue();
        Job job=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        deadLetterQueue.add(job);
        assertEquals(1, deadLetterQueue.size());
        Job result=deadLetterQueue.poll();
        assertEquals(job, result);
        assertTrue(deadLetterQueue.isEmpty());
    }

    @Test
    void shouldReturnJobsInInsertionOrder(){
        DeadLetterQueue deadLetterQueue=new DeadLetterQueue();
        Job firstJob=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        Job secondJob=new Job("job-2", JobType.REPORT, JobPriority.LOW);
        deadLetterQueue.add(firstJob);
        deadLetterQueue.add(secondJob);
        assertEquals(firstJob, deadLetterQueue.poll());
        assertEquals(secondJob, deadLetterQueue.poll());
    }

    @Test
    void shouldReturnEmptyWhenQueueHasNoJobs(){
        DeadLetterQueue deadLetterQueue=new DeadLetterQueue();
        assertTrue(deadLetterQueue.isEmpty());
        assertEquals(0, deadLetterQueue.size());
        assertEquals(null, deadLetterQueue.poll());
    }

    @Test
    void shouldRejectNullJob(){
        DeadLetterQueue queue=new DeadLetterQueue();
        assertThrows(
            IllegalArgumentException.class,
            ()->queue.add(null)
        );
    }

}
