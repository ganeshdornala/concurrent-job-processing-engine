package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobExecutorImplementationTest {

    @Test
    void emailExecutorShouldExecuteEmailJob() {
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        JobExecutor executor = new EmailJobExecutor();
        assertEquals("Executed email job: job-1",executor.execute(job));
    }

    @Test
    void reportExecutorShouldExecuteReportJob() {
        Job job = new Job("job-2",JobType.REPORT,JobPriority.MEDIUM);
        JobExecutor executor = new ReportJobExecutor();
        assertEquals("Executed report job: job-2",executor.execute(job));
    }

    @Test
    void dataProcessingExecutorShouldExecuteDataProcessingJob() {
        Job job = new Job("job-3",JobType.DATA_PROCESSING,JobPriority.LOW);
        JobExecutor executor = new DataProcessingJobExecutor();
        assertEquals("Executed data processing job: job-3",executor.execute(job));
    }

}
