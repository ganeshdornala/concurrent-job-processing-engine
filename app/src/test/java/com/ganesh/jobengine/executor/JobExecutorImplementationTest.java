package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JobExecutorImplementationTest {

    @Test
    void emailExecutorShouldExecuteEmailJob() {
        Job job = new Job("job-1",JobType.EMAIL,JobPriority.HIGH);
        JobExecutor executor = new EmailJobExecutor();
        assertDoesNotThrow(()->executor.execute(job));
    }

    @Test
    void reportExecutorShouldExecuteReportJob() {
        Job job = new Job("job-2",JobType.REPORT,JobPriority.MEDIUM);
        JobExecutor executor = new ReportJobExecutor();
        assertDoesNotThrow(()->executor.execute(job));
    }

    @Test
    void dataProcessingExecutorShouldExecuteDataProcessingJob() {
        Job job = new Job("job-3",JobType.DATA_PROCESSING,JobPriority.LOW);
        JobExecutor executor = new DataProcessingJobExecutor();
        assertDoesNotThrow(()->executor.execute(job));
    }

}
