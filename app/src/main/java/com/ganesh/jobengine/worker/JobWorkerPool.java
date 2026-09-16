package com.ganesh.jobengine.worker;

import com.ganesh.jobengine.queue.JobQueue;

import com.ganesh.jobengine.persistence.JobRepository;

import com.ganesh.jobengine.deadletter.DeadLetterQueue;
import com.ganesh.jobengine.executor.JobExecutorRegistry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JobWorkerPool {

    private final ExecutorService executorService;
    private final int workerCount;
    private final JobRepository jobRepository;
    private final DeadLetterQueue deadLetterQueue;
    private final JobExecutorRegistry executorRegistry;

    public JobWorkerPool(int workerCount) {
        this(
            workerCount,
            null,
            null,
            new JobExecutorRegistry()
        );
    }

    public JobWorkerPool(
            int workerCount,
            JobRepository jobRepository) {
        this(
            workerCount,
            jobRepository,
            null,
            new JobExecutorRegistry()
        );
    }

    public JobWorkerPool(
            int workerCount,
            JobRepository jobRepository,
            DeadLetterQueue deadLetterQueue) {
        this(
            workerCount,
            jobRepository,
            deadLetterQueue,
            new JobExecutorRegistry()
        );
    }

    public JobWorkerPool(
            int workerCount,
            JobRepository jobRepository,
            DeadLetterQueue deadLetterQueue,
            JobExecutorRegistry executorRegistry) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException(
                "Worker count must be greater than zero");
        }
        if (executorRegistry == null) {
            throw new IllegalArgumentException(
                "Executor registry cannot be null");
        }

        this.workerCount = workerCount;
        this.jobRepository = jobRepository;
        this.deadLetterQueue = deadLetterQueue;
        this.executorRegistry = executorRegistry;

        this.executorService = Executors.newFixedThreadPool(workerCount);
    }

    public void start(JobQueue jobQueue) {
        for (int i = 0; i < workerCount; i++) {
            executorService.submit(new JobWorker(
                    jobQueue,
                    executorRegistry,
                    jobRepository,
                    deadLetterQueue));
        }
    }

    public void shutdown() throws InterruptedException {
        executorService.shutdownNow();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException(
                    "Worker pool did not terminate within the timeout");
        }
    }

}
