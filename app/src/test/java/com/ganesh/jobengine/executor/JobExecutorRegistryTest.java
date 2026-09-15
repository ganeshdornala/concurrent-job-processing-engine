package com.ganesh.jobengine.executor;

import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class JobExecutorRegistryTest {

    @Test
    void registryShouldReturnEmailExecutor() {
        JobExecutorRegistry registry = new JobExecutorRegistry();
        assertInstanceOf(EmailJobExecutor.class,registry.getExecutor(JobType.EMAIL));
    }

    @Test
    void registryShouldReturnReportExecutor() {
        JobExecutorRegistry registry = new JobExecutorRegistry();
        assertInstanceOf(ReportJobExecutor.class,registry.getExecutor(JobType.REPORT));
    }

    @Test
    void registryShouldReturnDataProcessingExecutor() {
        JobExecutorRegistry registry = new JobExecutorRegistry();
        assertInstanceOf(DataProcessingJobExecutor.class,registry.getExecutor(JobType.DATA_PROCESSING));
    }

    @Test
    void registryShouldAllowCustomExecutorRegistration() {
        JobExecutorRegistry registry = new JobExecutorRegistry();
        JobExecutor customExecutor = job -> "Custom execution: " + job.getId();
        registry.register(JobType.EMAIL, customExecutor);
        JobExecutor executor = registry.getExecutor(JobType.EMAIL);
        assertEquals(
            "Custom execution: job-1",
            executor.execute(
                new com.ganesh.jobengine.domain.Job("job-1",JobType.EMAIL,com.ganesh.jobengine.domain.JobPriority.HIGH)
            )
        );
    }

}