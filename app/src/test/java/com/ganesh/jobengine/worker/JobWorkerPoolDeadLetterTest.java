package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.deadletter.DeadLetterQueue;
import com.ganesh.jobengine.domain.Job;
import com.ganesh.jobengine.domain.JobPriority;
import com.ganesh.jobengine.domain.JobStatus;
import com.ganesh.jobengine.domain.JobType;
import com.ganesh.jobengine.executor.JobExecutorRegistry;
import com.ganesh.jobengine.persistence.JobRepository;
import com.ganesh.jobengine.queue.JobQueue;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobWorkerPoolDeadLetterTest {

    @Test
    void shouldMoveFailedJobToDeadLetterQueue()
            throws InterruptedException {

        JobQueue jobQueue = new JobQueue();

        DeadLetterQueue deadLetterQueue = new DeadLetterQueue();

        Job job = new Job(
                "job-1",
                JobType.EMAIL,
                JobPriority.HIGH);

        jobQueue.add(job);

        JobExecutorRegistry executorRegistry = new JobExecutorRegistry();

        executorRegistry.register(
                JobType.EMAIL,
                ignored -> {
                    throw new RuntimeException(
                            "Email service unavailable");
                });

        JobRepository repository = new JobRepository() {

            @Override
            public void save(Job job) {
            }

            @Override
            public Optional<Job> findById(String jobId) {
                return Optional.empty();
            }
        };

        JobWorkerPool workerPool = new JobWorkerPool(
                1,
                repository,
                deadLetterQueue,
                executorRegistry);

        workerPool.start(jobQueue);

        long timeout = System.currentTimeMillis() + 3000;

        while (deadLetterQueue.isEmpty()
                && System.currentTimeMillis() < timeout) {
            Thread.sleep(10);
        }

        workerPool.shutdown();

        assertEquals(
                JobStatus.FAILED,
                job.getStatus());

        assertEquals(
                3,
                job.getExecutionCount());

        assertEquals(
                1,
                deadLetterQueue.size());

        assertEquals(
                job,
                deadLetterQueue.poll());
    }
}