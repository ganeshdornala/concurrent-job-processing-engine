# Concurrent Job Processing Engine

A Java-based concurrent job processing engine designed to execute jobs using a priority-based queue and a configurable worker pool, with persistence, retry handling, and dead-letter job management.

## Overview

The Concurrent Job Processing Engine demonstrates how a backend job-processing system can be built using core Java and Java concurrency features.

The project focuses on:

- Object-oriented design
- Java Collections
- Generics and functional interfaces
- Priority-based job queuing
- Concurrent worker execution
- Thread-safe state management
- File-based persistence
- Retry handling
- Dead-letter job processing

## Architecture

```text
REST API
   ↓
Job Queue
   ↓
Worker Pool
   ↓
Concurrent Execution
   ↓
Persistence
   ↓
Retry
   ↓
Dead-letter Jobs
```

The current implementation focuses on the Java processing engine. The REST API layer is part of the planned overall architecture and will be implemented in a later phase.

## Core Components

### Job

Represents a unit of work.

Each job contains:

- Job ID
- Job type
- Priority
- Status
- Execution count
- Failure information
- Execution history

### Job Queue

Uses a thread-safe priority queue to determine the order in which jobs are processed.

Jobs are ordered by:

- Priority — HIGH, MEDIUM, LOW
- Submission sequence — maintains deterministic ordering for jobs with the same priority

### Worker Pool

Uses Java's ExecutorService with a fixed-size thread pool.

Multiple workers can process jobs concurrently from the shared job queue.

### Job Executors

Different job types are handled through dedicated executors:

- Email
- Report
- Data Processing

A JobExecutorRegistry maps each job type to its corresponding executor.

### Persistence

Jobs are persisted using a file-based repository.

The repository stores:

- Job metadata
- Current status
- Execution count
- Failure information
- Execution history
- Job sequence

Java Properties files are used for storage.

### Retry Handling

When a job execution fails:

- The failure is recorded.
- The execution history is updated.
- The retry count is checked.
- If retry attempts remain, the job is returned to the queue.
- Otherwise, the job is moved to the dead-letter queue.

### Dead-letter Queue

Jobs that exceed the maximum number of execution attempts are moved to a dedicated dead-letter queue instead of being retried indefinitely.

## Java Concepts Demonstrated

### Core Java

- Classes and objects
- Constructors
- Methods
- Strings
- Enums
- Exception handling

### Object-Oriented Programming

- Encapsulation
- Abstraction
- Interfaces
- Polymorphism

### Collections

- List
- Map
- EnumMap
- PriorityQueue concepts
- BlockingQueue
- PriorityBlockingQueue

### Modern Java

- Generics
- Lambda expressions
- Functional interfaces
- Switch expressions
- Optional
- Enums

### Concurrency

- Threads
- Runnable
- ExecutorService
- Fixed thread pools
- BlockingQueue
- Thread-safe counters
- Synchronization
- Graceful worker shutdown

### File I/O

- Path
- Files
- Reader
- Writer
- Try-with-resources
- File-based persistence

## Project Structure

```text
app/src/main/java/com/ganesh/jobengine/
├── Application.java
├── deadletter/
│   └── DeadLetterQueue.java
├── domain/
│   ├── Job.java
│   ├── JobExecution.java
│   ├── JobPriority.java
│   ├── JobStatus.java
│   └── JobType.java
├── executor/
│   ├── JobExecutor.java
│   ├── JobExecutorRegistry.java
│   ├── EmailJobExecutor.java
│   ├── ReportJobExecutor.java
│   └── DataProcessingJobExecutor.java
├── persistence/
│   ├── JobRepository.java
│   └── FileJobRepository.java
├── queue/
│   ├── JobPriorityComparator.java
│   └── JobQueue.java
└── worker/
    ├── JobWorker.java
    └── JobWorkerPool.java
```

## Testing

The project includes tests covering:

- Job creation and validation
- Job state management
- Concurrent execution state
- Priority ordering
- Queue behavior
- Worker processing
- Worker pool behavior
- Executor registration and delegation
- Execution history
- Failure handling
- Retry behavior
- Persistence
- Dead-letter handling

Run the complete test suite with:

```bash
.\gradlew clean test
```

## Requirements

- Java 21
- Gradle 9.7.1

The project uses the Gradle Wrapper, so Gradle does not need to be installed separately.

## Running the Project

Clone the repository:

```bash
git clone https://github.com/ganeshdornala/concurrent-job-processing-engine.git
cd concurrent-job-processing-engine
```

Run the application:

```bash
.\gradlew :app:run
```

Run tests:

```bash
.\gradlew clean test
```

## Build

```bash
.\gradlew build
```

## Tech Stack

- Java 21
- Gradle
- JUnit Jupiter
- Git
- GitHub

## Project Status

Current implementation includes:

- Job domain model
- Priority-based job queue
- Concurrent worker pool
- Job executors
- Execution history
- File-based persistence
- Retry handling
- Dead-letter queue
- Automated tests

The project is being developed incrementally alongside the Java learning roadmap.

## Author

Ganesh Dornala

GitHub: https://github.com/ganeshdornala
