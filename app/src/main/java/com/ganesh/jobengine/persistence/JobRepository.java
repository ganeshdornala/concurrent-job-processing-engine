package com.ganesh.jobengine.persistence;

import com.ganesh.jobengine.domain.Job;

import java.io.IOException;
import java.util.Optional;

public interface JobRepository {

    void save(Job job) throws IOException;

    Optional<Job> findById(String jobId) throws IOException;
}