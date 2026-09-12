package com.ganesh.jobengine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobConcurrencyTest {
    
    @Test
    void  multipleThreadsShouldIncrementExecutionCount() throws InterruptedException{
        Job job=new Job("job-concurrent", JobType.DATA_PROCESSING, JobPriority.HIGH);
        int threadCount=10;
        int incrementsPerThread=10_000;
        Thread[] threads=new Thread[threadCount];
        for(int i=0;i<threadCount;i++){
            threads[i]=new Thread(()->{
                for(int j=0;j<incrementsPerThread;j++){
                    job.incrementExecutionCount();
                }
            });
        }
        for(Thread thread:threads){
            thread.start();
        }
        for(Thread thread:threads){
            thread.join();
        }
        int expectedCount=threadCount*incrementsPerThread;
        assertEquals(expectedCount, job.getExecutionCount());
    }

}
