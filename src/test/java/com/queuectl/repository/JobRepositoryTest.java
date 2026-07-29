package com.queuectl.repository;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.model.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JobRepositoryTest {

    private Database database;
    private JobRepository jobRepository;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path dbPath = tempDir.resolve("test_queuectl.db");
        database = new Database(dbPath.toString());
        database.migrate();
        jobRepository = new JobRepository(database);
    }

    @Test
    void testInsertAndFindById() {
        long now = System.currentTimeMillis();
        Job job = new Job(null, "echo hello", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now);

        Job inserted = jobRepository.insert(job);
        assertNotNull(inserted.getId());

        Optional<Job> found = jobRepository.findById(inserted.getId());
        assertTrue(found.isPresent());
        assertEquals("echo hello", found.get().getCommand());
        assertEquals(JobStatus.PENDING, found.get().getStatus());
        assertEquals(5, found.get().getMaxAttempts());
    }

    @Test
    void testFindAllAndFindByStatus() {
        long now = System.currentTimeMillis();
        Job job1 = new Job(null, "job1", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now);
        Job job2 = new Job(null, "job2", JobStatus.RUNNING, 1, 5, now, now, "worker-1", now + 10000, null, now, now);
        jobRepository.insert(job1);
        jobRepository.insert(job2);

        List<Job> allJobs = jobRepository.findAll();
        assertEquals(2, allJobs.size());

        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);
        assertEquals(1, pendingJobs.size());
        assertEquals("job1", pendingJobs.get(0).getCommand());

        List<Job> runningJobs = jobRepository.findByStatus(JobStatus.RUNNING);
        assertEquals(1, runningJobs.size());
        assertEquals("job2", runningJobs.get(0).getCommand());
    }

    @Test
    void testCountByStatus() {
        long now = System.currentTimeMillis();
        jobRepository.insert(new Job(null, "j1", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));
        jobRepository.insert(new Job(null, "j2", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));
        jobRepository.insert(new Job(null, "j3", JobStatus.SUCCEEDED, 1, 5, now, null, null, null, null, now, now));

        Map<JobStatus, Long> counts = jobRepository.countByStatus();
        assertEquals(2L, counts.get(JobStatus.PENDING));
        assertEquals(1L, counts.get(JobStatus.SUCCEEDED));
        assertEquals(0L, counts.get(JobStatus.FAILED));
        assertEquals(0L, counts.get(JobStatus.RUNNING));
        assertEquals(0L, counts.get(JobStatus.DEAD));
    }

    @Test
    void testUpdate() {
        long now = System.currentTimeMillis();
        Job job = jobRepository.insert(new Job(null, "task", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));

        job.setStatus(JobStatus.SUCCEEDED);
        job.setAttempts(1);
        job.setUpdatedAt(now + 500);

        boolean updated = jobRepository.update(job);
        assertTrue(updated);

        Optional<Job> found = jobRepository.findById(job.getId());
        assertTrue(found.isPresent());
        assertEquals(JobStatus.SUCCEEDED, found.get().getStatus());
        assertEquals(1, found.get().getAttempts());
    }

    @Test
    void testDelete() {
        long now = System.currentTimeMillis();
        Job job = jobRepository.insert(new Job(null, "to-delete", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));

        boolean deleted = jobRepository.delete(job.getId());
        assertTrue(deleted);

        Optional<Job> found = jobRepository.findById(job.getId());
        assertFalse(found.isPresent());
    }
}
