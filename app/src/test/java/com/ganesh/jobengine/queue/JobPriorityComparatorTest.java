package com.ganesh.jobengine.queue;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JobPriorityComparatorTest {
    
    private final Comparator<Job> comparator=new JobPriorityComparator();

    @Test 
    void highPriorityShouldComeBeforeMediumPriority(){
        Job high=new Job("job-high",JobType.EMAIL,JobPriority.HIGH);
        Job medium=new Job("job-medium",JobType.EMAIL,JobPriority.MEDIUM);

        assertTrue(comparator.compare(high, medium)<0);
    }

    @Test 
    void mediumPriorityShouldComeBeforeLowPriority(){
        Job medium=new Job("job-medium",JobType.EMAIL,JobPriority.MEDIUM);
        Job low=new Job("job-low",JobType.EMAIL,JobPriority.LOW);

        assertTrue(comparator.compare(medium,low)<0);
    }

    @Test 
    void highPriorityShouldComeBeforeLowPriority(){
        Job high=new Job("job-high",JobType.EMAIL,JobPriority.HIGH);
        Job low=new Job("job-low",JobType.EMAIL,JobPriority.LOW);

        assertTrue(comparator.compare(high,low)<0);
    }

}
