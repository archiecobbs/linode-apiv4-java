
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.workers;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.dellroad.linode.apiv4.model.Image;
import org.dellroad.linode.apiv4.spring.SpringTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class WorkerTest extends SpringTest {

    private static final String DEFAULT_IMAGE_VENDOR = "openSUSE";
    private static final String DEFAULT_REGION_ID = "us-southeast-1a";

    private final WorkerPool workerPool = new WorkerPool();

    private ThreadPoolTaskScheduler taskScheduler;
    private Image image;

    @BeforeClass
    public void startupTaskScheduler() throws Exception {
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(3);
        this.taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        this.taskScheduler.afterPropertiesSet();
    }

    @AfterClass(dependsOnMethods = "shutdownWorkerPool")
    public void shutdownTaskScheduler() throws Exception {
        this.taskScheduler.destroy();
    }

    @BeforeClass
    public void determineImage() throws Exception {
        this.image = this.newestPublicVendorImage(DEFAULT_IMAGE_VENDOR);
    }

    @BeforeClass(dependsOnMethods = "startupTaskScheduler")
    @Parameters("workerPoolGroupName")
    public void startupWorkerPool(@Optional String workerPoolGroupName) throws Exception {
        this.workerPool.setGroupName(workerPoolGroupName != null && !workerPoolGroupName.isEmpty() ?
          workerPoolGroupName : this.getClass().getSimpleName() + " " + this.random.nextInt(1000000));
        this.workerPool.setMaxIdleTime(90);
        this.workerPool.setRequestSender(this.sender);
        this.workerPool.setTaskExecutor(this.taskExecutor);
        this.workerPool.setTaskScheduler(this.taskScheduler);
        final String rootPassword = this.workerPool.generateRandomPassword();
        this.log.info("startupWorkerPool(): standard root password is \"{}\"", rootPassword);
        this.workerPool.setStandardRootPassword(rootPassword);
        final List<String> standardSshFlags = this.workerPool.getStandardSshFlags();
        standardSshFlags.addAll(Arrays.asList(
          "-oStrictHostKeyChecking=no",
          "-oConnectTimeout=5",
          "-oCheckHostIP=no",
          "-oUserKnownHostsFile=/dev/null"));
        this.workerPool.setStandardSshFlags(standardSshFlags);
        this.workerPool.start();
    }

    @AfterClass
    public void shutdownWorkerPool() throws Exception {
        this.workerPool.stop();
    }

    @Test(dependsOnMethods = "verifyAuthToken")
    public void testWorkerPool() throws Exception {

        // Snapshot current worker pool
        final Set<Worker> startingPool = this.workerPool.getWorkers();
        this.log.info("testWorkerPool(): starting worker set: {}", startingPool);

        // Add a worker
        this.log.info("testWorkerPool(): adding new worker...");
        final Worker worker = this.workerPool.addWorker(DEFAULT_REGION_ID,
          this.cheapestType().getId(), this.image.getId(), -1, -1, null, null);
        this.log.info("testWorkerPool(): successfully added new worker: {}", worker);
        this.log.info("testWorkerPool(): new worker pool: {}", this.workerPool.getWorkers());

        // Wait for worker to become available
        this.log.info("testWorkerPool(): waiting up to {} seconds for {} to become alive...",
          this.workerPool.getMaxStartupTime(), worker);
        final Instant startupDeadline = Instant.now().plus(Duration.ofSeconds(this.workerPool.getMaxStartupTime()));
    startupLoop:
        while (Instant.now().isBefore(startupDeadline)) {

            // Check state
            final Set<Worker> workers = this.workerPool.getWorkers();
            assert workers.size() == startingPool.size() + 1 : "wrong number of workers: " + workers;
            assert workers.contains(worker) : worker + " not found in " + workers;
            switch (worker.getState()) {
            case CREATING:
                break;
            case RUNNING:
                break startupLoop;
            default:
                assert false : "unexpected worker state for " + worker;
                break;
            }

            // Sleep
            Thread.sleep(5000);
            this.log.info("testWorkerPool(): check worker: {}", worker);
        }

        // Execute a task
        final Process process = worker.execute(Arrays.asList("exit", "37"));
        assert process.waitFor(10, TimeUnit.SECONDS) : process + " did not complete within 10 seconds";
        final int exitValue = process.exitValue();
        if (exitValue != 37) {
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            final byte[] bytes = new byte[100];
            for (int r; (r = process.getErrorStream().read(bytes)) != -1; )
                buf.write(bytes, 0, r);
            assert false : "process returned " + exitValue + " != 37: " + new String(buf.toByteArray(), StandardCharsets.UTF_8);
        }

        // Shutdown worker
        assert worker.destroy() : "worker.destroy() failed for " + worker;

        // Wait for worker to shutdown
        this.log.info("testWorkerPool(): waiting up to {} seconds for {} to shutdown...",
          this.workerPool.getMaxShutdownTime(), worker);
        final Instant shutdownDeadline = Instant.now().plus(Duration.ofSeconds(this.workerPool.getMaxShutdownTime()));
    shutdownLoop:
        while (Instant.now().isBefore(shutdownDeadline)) {

            // Check state
            final Set<Worker> workers = this.workerPool.getWorkers();
            if (workers.size() == startingPool.size()) {
                assert worker.getState().equals(Worker.State.INVALID);
                break shutdownLoop;
            }
            if (workers.size() != startingPool.size() + 1)
                assert false : "wrong number of workers: " + workers;
            assert workers.contains(worker);
            switch (worker.getState()) {
            case DESTROYING:
            case INVALID:
                break;
            default:
                assert false : "unexpected worker state for " + worker;
                break;
            }

            // Sleep
            Thread.sleep(5000);
            this.log.info("testWorkerPool(): check worker: {}", worker);
        }

        // Done
        this.log.info("testWorkerPool(): ending worker set: {}", this.workerPool.getWorkers());
    }
}
