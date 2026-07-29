package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.model.JobStatus;
import com.queuectl.repository.JobRepository;
import picocli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ListCommandTest {

    private JobRepository jobRepository;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path dbPath = tempDir.resolve("list_test.db");
        Database database = new Database(dbPath.toString());
        database.migrate();
        jobRepository = new JobRepository(database);
    }

    @Test
    void testListEmpty() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            ListCommand listCommand = new ListCommand(jobRepository);
            CommandLine cmd = new CommandLine(listCommand);
            int exitCode = cmd.execute();

            assertEquals(0, exitCode);
            assertTrue(outContent.toString().contains("No jobs found."));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testListAllJobs() {
        long now = System.currentTimeMillis();
        jobRepository.insert(new Job(null, "echo 1", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));
        jobRepository.insert(new Job(null, "echo 2", JobStatus.RUNNING, 1, 5, now, null, null, null, null, now + 10, now + 10));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            ListCommand listCommand = new ListCommand(jobRepository);
            CommandLine cmd = new CommandLine(listCommand);
            int exitCode = cmd.execute();

            assertEquals(0, exitCode);
            String output = outContent.toString();
            assertTrue(output.contains("ID"));
            assertTrue(output.contains("STATUS"));
            assertTrue(output.contains("ATTEMPTS"));
            assertTrue(output.contains("MAX"));
            assertTrue(output.contains("COMMAND"));
            assertTrue(output.contains("echo 1"));
            assertTrue(output.contains("echo 2"));
            assertTrue(output.contains("PENDING"));
            assertTrue(output.contains("RUNNING"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testListWithStatusFilter() {
        long now = System.currentTimeMillis();
        jobRepository.insert(new Job(null, "echo 1", JobStatus.PENDING, 0, 5, now, null, null, null, null, now, now));
        jobRepository.insert(new Job(null, "echo 2", JobStatus.RUNNING, 1, 5, now, null, null, null, null, now + 10, now + 10));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            ListCommand listCommand = new ListCommand(jobRepository);
            CommandLine cmd = new CommandLine(listCommand);
            int exitCode = cmd.execute("--status", "PENDING");

            assertEquals(0, exitCode);
            String output = outContent.toString();
            assertTrue(output.contains("echo 1"));
            assertTrue(output.contains("PENDING"));
            assertFalse(output.contains("echo 2"));
            assertFalse(output.contains("RUNNING"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testListInvalidStatusFilter() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            ListCommand listCommand = new ListCommand(jobRepository);
            CommandLine cmd = new CommandLine(listCommand);
            cmd.execute("--status", "INVALID_STATUS");

            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Unknown job status: INVALID_STATUS"));
            assertTrue(errorOutput.contains("Allowed statuses: PENDING, RUNNING, SUCCEEDED, FAILED, DEAD"));
        } finally {
            System.setErr(originalErr);
        }
    }
}
