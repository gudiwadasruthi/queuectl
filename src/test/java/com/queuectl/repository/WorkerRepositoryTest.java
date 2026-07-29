package com.queuectl.repository;

import com.queuectl.db.Database;
import com.queuectl.model.WorkerInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WorkerRepositoryTest {

    private Database database;
    private WorkerRepository workerRepository;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path dbPath = tempDir.resolve("test_workers.db");
        database = new Database(dbPath.toString());
        database.migrate();
        workerRepository = new WorkerRepository(database);
    }

    @Test
    void testRegisterAndFindWorker() {
        long now = System.currentTimeMillis();
        WorkerInfo worker = new WorkerInfo("worker-1", now, now, "RUNNING");

        WorkerInfo registered = workerRepository.registerWorker(worker);
        assertEquals("worker-1", registered.getId());

        Optional<WorkerInfo> found = workerRepository.findWorker("worker-1");
        assertTrue(found.isPresent());
        assertEquals("RUNNING", found.get().getStatus());
        assertEquals(now, found.get().getStartedAt());
    }

    @Test
    void testFindAllWorkers() {
        long now = System.currentTimeMillis();
        workerRepository.registerWorker(new WorkerInfo("w1", now, now, "RUNNING"));
        workerRepository.registerWorker(new WorkerInfo("w2", now, now, "STOPPED"));

        List<WorkerInfo> workers = workerRepository.findAllWorkers();
        assertEquals(2, workers.size());
    }

    @Test
    void testUpdateHeartbeatAndStatus() {
        long now = System.currentTimeMillis();
        workerRepository.registerWorker(new WorkerInfo("worker-1", now, now, "RUNNING"));

        long nextHeartbeat = now + 5000;
        boolean heartbeatUpdated = workerRepository.updateHeartbeat("worker-1", nextHeartbeat);
        assertTrue(heartbeatUpdated);

        boolean statusUpdated = workerRepository.updateStatus("worker-1", "STOPPING");
        assertTrue(statusUpdated);

        Optional<WorkerInfo> found = workerRepository.findWorker("worker-1");
        assertTrue(found.isPresent());
        assertEquals(nextHeartbeat, found.get().getLastHeartbeatAt());
        assertEquals("STOPPING", found.get().getStatus());
    }

    @Test
    void testDeleteWorker() {
        long now = System.currentTimeMillis();
        workerRepository.registerWorker(new WorkerInfo("worker-1", now, now, "RUNNING"));

        boolean deleted = workerRepository.deleteWorker("worker-1");
        assertTrue(deleted);

        Optional<WorkerInfo> found = workerRepository.findWorker("worker-1");
        assertFalse(found.isPresent());
    }
}
