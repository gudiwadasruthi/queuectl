package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.repository.JobRepository;
import picocli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnqueueCommandTest {

    private JobRepository jobRepository;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path dbPath = tempDir.resolve("enqueue_test.db");
        Database database = new Database(dbPath.toString());
        database.migrate();
        jobRepository = new JobRepository(database);
    }

    @Test
    void testEnqueueSuccess() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            EnqueueCommand enqueueCommand = new EnqueueCommand(jobRepository);
            CommandLine cmd = new CommandLine(enqueueCommand);
            int exitCode = cmd.execute("echo 'hello world'", "--max-attempts", "3");

            assertEquals(0, exitCode);
            String output = outContent.toString();
            assertTrue(output.contains("Job enqueued successfully."));
            assertTrue(output.contains("Job ID: 1"));
            assertTrue(output.contains("Command: echo 'hello world'"));
            assertTrue(output.contains("Status: PENDING"));
            assertTrue(output.contains("Max Attempts: 3"));

            List<Job> jobs = jobRepository.findAll();
            assertEquals(1, jobs.size());
            assertEquals("echo 'hello world'", jobs.get(0).getCommand());
            assertEquals(3, jobs.get(0).getMaxAttempts());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testEnqueueValidationBlankCommand() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            EnqueueCommand enqueueCommand = new EnqueueCommand(jobRepository);
            CommandLine cmd = new CommandLine(enqueueCommand);
            cmd.execute("   ");

            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Error: Command cannot be empty or blank."));
            assertTrue(jobRepository.findAll().isEmpty());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void testEnqueueValidationInvalidMaxAttempts() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            EnqueueCommand enqueueCommand = new EnqueueCommand(jobRepository);
            CommandLine cmd = new CommandLine(enqueueCommand);
            cmd.execute("echo test", "--max-attempts", "0");

            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("Error: Max attempts must be greater than 0."));
            assertTrue(jobRepository.findAll().isEmpty());
        } finally {
            System.setErr(originalErr);
        }
    }
}
