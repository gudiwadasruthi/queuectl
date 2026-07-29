package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.model.JobStatus;
import com.queuectl.model.WorkerInfo;
import com.queuectl.repository.JobRepository;
import com.queuectl.repository.WorkerRepository;
import picocli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StatusCommandTest {

    private JobRepository jobRepository;
    private WorkerRepository workerRepository;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path dbPath = tempDir.resolve("status_test.db");
        Database database = new Database(dbPath.toString());
        database.migrate();
        jobRepository = new JobRepository(database);
        workerRepository = new WorkerRepository(database);
    }

    @Test
    void testStatusCommandOutput() {
        long now = System.currentTimeMillis();
        jobRepository.insert(new Job(null, "j1", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));
        jobRepository.insert(new Job(null, "j2", JobStatus.SUCCEEDED, 1, 5, now, null, null, null, null, now, now));

        workerRepository.registerWorker(new WorkerInfo("w-1", now, now, "RUNNING"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            StatusCommand statusCommand = new StatusCommand(jobRepository, workerRepository);
            CommandLine cmd = new CommandLine(statusCommand);
            int exitCode = cmd.execute();

            assertEquals(0, exitCode);
            String output = outContent.toString();
            assertTrue(output.contains("Queue Summary"));
            assertTrue(output.contains("Pending:    1"));
            assertTrue(output.contains("Succeeded:  1"));
            assertTrue(output.contains("Worker Summary"));
            assertTrue(output.contains("Total Workers:      1"));
            assertTrue(output.contains("Running Workers:    1"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
