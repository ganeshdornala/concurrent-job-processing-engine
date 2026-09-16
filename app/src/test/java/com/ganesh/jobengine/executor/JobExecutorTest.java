package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JobExecutorTest {

    @Test 
    void executorShouldExecuteJob() {
        Job job=new Job("job-1", JobType.EMAIL, JobPriority.HIGH);
        JobExecutor executor=jobToExecute->{
        };
        assertDoesNotThrow(() -> executor.execute(job));
    }

}
