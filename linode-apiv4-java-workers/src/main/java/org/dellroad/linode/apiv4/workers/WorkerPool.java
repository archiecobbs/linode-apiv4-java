
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.workers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.dellroad.linode.apiv4.filter.Filter;
import org.dellroad.linode.apiv4.filter.FilterBuilder;
import org.dellroad.linode.apiv4.model.Linode;
import org.dellroad.linode.apiv4.request.CreateLinodeRequest;
import org.dellroad.linode.apiv4.spring.LinodeApiRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;

/**
 * A managed pool of Linode workers, on which commands may be executed remotely via SSH.
 *
 * <p><b>Linode Naming Convention</b>
 *
 * <p>
 * A worker pool has a {@linkplain #setGroupName group name}, which is used as the Linode display group for workers.
 * See {@link #setGroupName setGroupName()} for group naming requirements.
 * The group name is also a unique naming prefix for all workers in the pool. For example, if the worker pool group name is
 * {@code "My-Worker-Pool"} then worker Linodes will be named {@code "My-Worker-Pool-1"}, {@code "My-Worker-Pool-2"}, etc.
 *
 * <p>
 * All Linodes with other names are ignored. However on startup, any workers in the pool that already exist (identified
 * by their names) are automatically detected and added as members of the pool. This allows a worker pool's Linodes to
 * persist across restarts of a {@link WorkerPool} instance.
 *
 * <p><b>Pool Size</b>
 *
 * <p>
 * Workers are added to the pool via invocations of {@link #addWorker addWorker()}. Workers are never automatically added; however,
 * idle workers (those with no remaining {@link Process}es) are automatically shutdown after a configurable
 * {@linkplain #getMaxIdleTime maximum idle time}, until the configurable {@linkplain #getMinWorkers minimum number of
 * workers} is reached.
 *
 * <p><b>Managed vs. Unmanaged Workers</b>
 *
 * <p>
 * Workers are normally expected to follow specific {@linkplain Worker.State state transitions} as they are created, utilized,
 * and shutdown. If a worker is seen doing something unexpected, it transitions to {@link Worker.State#UNMANAGED}. In particular,
 * once a worker is created, it is expected to become {@link org.dellroad.linode.apiv4.model.Linode.Status#RUNNING} within the
 * {@linkplain #setMaxStartupTime configured maximum startup time}; similarly, when a worker is destroyed, it is expected
 * to disappear within the {@linkplain #setMaxShutdownTime configured maximum shutdown time}. Workers can also be manually
 * controlled via {@link Worker#setUnmanaged} and {@link Worker#setManaged}.
 *
 * <p>
 * If a {@link Worker}'s Linode disappears unexpectedly (including the situation where a new Linode with the same name
 * but different ID replaces it), the {@link Worker} becomes {@link Worker.State#INVALID} and is removed from the pool.
 * If a {@link WorkerPool} is {@link #stop}ped, all remaining workers immediately become {@link Worker.State#INVALID}.
 * A worker in the {@link Worker.State#INVALID} is no longer associated with its {@link WorkerPool} instance.
 * Use {@link #getWorkers} to inspect and monitor the current workers.
 *
 * <p>
 * In general, {@link WorkerPool}s are robust in the face of unexpected behavior. This allows humans to intervene when needed.
 * For example, if a worker is misbehaving or needs to be inspected by a human, you can invoke {@link Worker#setUnmanaged},
 * rename the worker in the Linode console, or shut it down.
 */
@ThreadSafe
public class WorkerPool {

    /**
     * Default minimum number of workers ({@value #DEFAULT_MIN_WORKERS}).
     */
    public static final int DEFAULT_MIN_WORKERS = 0;

    /**
     * Default maximum time (in seconds) for a newly created worker to be up and running
     * ({@value #DEFAULT_MAX_STARTUP_TIME_SECONDS}).
     *
     * <p>
     * If startup takes longer than this, the worker reverts to {@link Worker.State#UNMANAGED}.
     */
    public static final int DEFAULT_MAX_STARTUP_TIME_SECONDS = 600;

    /**
     * Default maximum time (in seconds) for a destroyed worker to disappear
     * ({@value #DEFAULT_MAX_SHUTDOWN_TIME_SECONDS}).
     *
     * <p>
     * If shutdown takes longer than this, the worker reverts to {@link Worker.State#UNMANAGED}.
     */
    public static final int DEFAULT_MAX_SHUTDOWN_TIME_SECONDS = 300;

    /**
     * Default maximum idle time (in seconds) before shutting down idle workers ({@value #DEFAULT_MAX_IDLE_TIME_SECONDS}).
     *
     * <p>
     * An idle worker is one with zero active {@link Process}es running on it.
     */
    public static final int DEFAULT_MAX_IDLE_TIME_SECONDS = 300;

    /**
     * Default Linode group name and worker name prefix ({@value #DEFAULT_GROUP_NAME}).
     *
     * @see #setGroupName setGroupName()
     */
    public static final String DEFAULT_GROUP_NAME = "Worker-Pool";

    /**
     * The number of hex digits in randomly generated passwords ({@value #RANDOM_PASSWORD_DIGITS}).
     *
     * @see #generateRandomPassword
     */
    private static final int RANDOM_PASSWORD_DIGITS = 32;

    private static final int SSH_PORT = 22;
    private static final int SSH_CHECK_TIMEOUT_SECONDS = 5;
    private static final int CHECK_INTERVAL_SECONDS = 10;
    private static final String GROUP_NAME_PATTERN = "[A-Za-z0-9]([-_]?[A-Za-z0-9])*";
    private static final Duration CHECK_INTERVAL = Duration.ofSeconds(CHECK_INTERVAL_SECONDS);

    private final Logger log = LoggerFactory.getLogger(this.getClass());

// Service beans

    @Autowired
    @GuardedBy("this")
    private LinodeApiRequestSender sender;

    @Autowired
    @GuardedBy("this")
    private AsyncTaskExecutor taskExecutor;

    @Autowired
    @GuardedBy("this")
    private TaskScheduler taskScheduler;

// Configuration

    @GuardedBy("this")
    private String groupName = DEFAULT_GROUP_NAME;
    @GuardedBy("this")
    private int minWorkers = DEFAULT_MIN_WORKERS;
    @GuardedBy("this")
    private int maxStartupTime = DEFAULT_MAX_STARTUP_TIME_SECONDS;
    @GuardedBy("this")
    private int maxShutdownTime = DEFAULT_MAX_SHUTDOWN_TIME_SECONDS;
    @GuardedBy("this")
    private int maxIdleTime = DEFAULT_MAX_IDLE_TIME_SECONDS;
    @GuardedBy("this")
    private String standardRootPassword;
    @GuardedBy("this")
    private List<String> standardSshFlags = Arrays.asList(new String[] {
      "-2xaT", "-oServerAliveInterval=10", "-oServerAliveCountMax=6", "-oTCPKeepAlive=yes"
    });

// State

    @GuardedBy("this")
    private boolean started;
    @GuardedBy("this")
    private final HashSet<Worker> workers = new HashSet<>();
    @GuardedBy("this")
    private ScheduledFuture<?> periodicCheckFuture;
    private final SecureRandom random = new SecureRandom();

// Constructors

    /**
     * Constructor.
     */
    public WorkerPool() {
    }

    /**
     * Constructor.
     *
     * @param groupName worker Linode group
     * @throws IllegalArgumentException if {@code groupName} is invalid
     */
    public WorkerPool(String groupName) {
        this.setGroupName(groupName);
    }

// Properties

    /**
     * Get the name of the worker Linode group.
     *
     * @return worker Linode group name
     */
    public synchronized String getGroupName() {
        return this.groupName;
    }

    /**
     * Configure the name of the Linode group in which to put workers.
     *
     * <p>
     * This setting also determines the name of each worker, by taking the group name and adding the worker index as a suffix.
     * For example, if the group name is {@code "My-Workers"} then the worker Linodes will be named {@code "My-Workers-1"},
     * {@code "My-Workers-2"}, etc.
     *
     * <p>
     * The only allowed characters are ASCII letters, digits, dashes, and underscores. The name must start and end
     * in a letter or digit, and each dash or underscore must be surrounded by a letter or digit.
     *
     * <p>
     * The default group name is {@value #DEFAULT_GROUP_NAME}.
     *
     * @param groupName worker Linode group
     * @throws IllegalArgumentException if {@code groupName} is invalid
     * @throws IllegalStateException if this instance is already {@link #start}ed
     */
    public synchronized void setGroupName(final String groupName) {
        if (this.started)
            throw new IllegalStateException("already started");
        if (groupName == null || groupName.isEmpty())
            throw new IllegalArgumentException("null or empty groupName");
        if (!Pattern.compile(GROUP_NAME_PATTERN).matcher(groupName).matches())
            throw new IllegalArgumentException("invalid characters in groupName");
        this.groupName = groupName;
    }

    /**
     * Get the minimum number of workers to keep alive in the worker pool, even if idle.
     *
     * <p>
     * Default is {@value #DEFAULT_MIN_WORKERS}.
     *
     * @return minumum number of workers to keep around
     */
    public synchronized int getMinWorkers() {
        return this.minWorkers;
    }

    /**
     * Set the minimum number of workers to keep alive in the worker pool.
     *
     * @param minWorkers  minimum number of workers to keep alive even if idle
     * @throws IllegalArgumentException if {@code minWorkers} is negative
     * @throws IllegalStateException if this instance is already {@link #start}ed
     */
    public synchronized void setMinWorkers(final int minWorkers) {
        if (minWorkers < 0)
            throw new IllegalArgumentException("minWorkers < 0");
        if (this.started)
            throw new IllegalStateException("already started");
        this.minWorkers = minWorkers;
    }

    /**
     * Get the maximum time to wait for a newly created worker to become up and running.
     *
     * <p>
     * If startup takes longer than this, the worker reverts to {@link Worker.State#UNMANAGED}.
     *
     * <p>
     * Default is {@value #DEFAULT_MAX_STARTUP_TIME_SECONDS} seconds.
     *
     * @return maxumum worker startup time in seconds
     */
    public synchronized int getMaxStartupTime() {
        return this.maxStartupTime;
    }

    /**
     * Set the maximum time to wait for a newly created worker to become up and running.
     *
     * @param maxStartupTime maxumum worker startup time in seconds
     * @throws IllegalStateException if this instance is already {@link #start}ed
     * @throws IllegalArgumentException if {@code maxStartupTime} is zero or negative
     */
    public synchronized void setMaxStartupTime(final int maxStartupTime) {
        if (maxStartupTime <= 0)
            throw new IllegalArgumentException("maxStartupTime <= 0");
        if (this.started)
            throw new IllegalStateException("already started");
        this.maxStartupTime = maxStartupTime;
    }

    /**
     * Get the maximum time to wait for a destroyed worker to disappear.
     *
     * <p>
     * If shutdown takes longer than this, the worker reverts to {@link Worker.State#UNMANAGED}.
     *
     * <p>
     * Default is {@value #DEFAULT_MAX_SHUTDOWN_TIME_SECONDS} seconds.
     *
     * @return maxumum worker shutdown time in seconds
     */
    public synchronized int getMaxShutdownTime() {
        return this.maxShutdownTime;
    }

    /**
     * Set the maximum time to wait for a destroyed worker to disappear.
     *
     * @param maxShutdownTime maxumum worker shutdown time in seconds
     * @throws IllegalStateException if this instance is already {@link #start}ed
     * @throws IllegalArgumentException if {@code maxShutdownTime} is zero or negative
     */
    public synchronized void setMaxShutdownTime(final int maxShutdownTime) {
        if (maxShutdownTime <= 0)
            throw new IllegalArgumentException("maxShutdownTime <= 0");
        if (this.started)
            throw new IllegalStateException("already started");
        this.maxShutdownTime = maxShutdownTime;
    }

    /**
     * Get the max idle time for an idle worker before destroying it.
     *
     * <p>
     * When a worker has zero processes for this long, and there are more than the {@linkplain #getMinWorkers minimum number
     * of workers}, then it will be destroyed.
     *
     * <p>
     * Default is {@value #DEFAULT_MAX_IDLE_TIME_SECONDS} seconds.
     *
     * @return maxumum worker idle time in seconds
     */
    public synchronized int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    /**
     * Set the maximum idle time for a worker before destroying it.
     *
     * @param maxIdleTime maxumum worker idle time in seconds
     * @throws IllegalStateException if this instance is already {@link #start}ed
     * @throws IllegalArgumentException if {@code maxIdleTime} is zero or negative
     */
    public synchronized void setMaxIdleTime(final int maxIdleTime) {
        if (maxIdleTime <= 0)
            throw new IllegalArgumentException("maxIdleTime <= 0");
        if (this.started)
            throw new IllegalStateException("already started");
        this.maxIdleTime = maxIdleTime;
    }

    /**
     * Configure the {@link LinodeApiRequestSender} used to access the Linode API.
     *
     * <p>
     * Required property (if not autowired by Spring).
     *
     * @param sender request sender
     * @throws IllegalStateException if this instance is already {@link #start}ed
     */
    public synchronized void setRequestSender(final LinodeApiRequestSender sender) {
        if (this.started)
            throw new IllegalStateException("already started");
        this.sender = sender;
    }

    /**
     * Configure the {@link AsyncTaskExecutor} used for asynchronous task execution.
     *
     * <p>
     * Required property (if not autowired by Spring).
     *
     * @param taskExecutor task executor
     * @throws IllegalStateException if this instance is already {@link #start}ed
     */
    public synchronized void setTaskExecutor(final AsyncTaskExecutor taskExecutor) {
        if (this.started)
            throw new IllegalStateException("already started");
        this.taskExecutor = taskExecutor;
    }

    /**
     * Configure the {@link TaskScheduler} used for scheduled tasks.
     *
     * <p>
     * Required property (if not autowired by Spring).
     *
     * @param taskScheduler task scheduler
     * @throws IllegalStateException if this instance is already {@link #start}ed
     */
    public synchronized void setTaskScheduler(final TaskScheduler taskScheduler) {
        if (this.started)
            throw new IllegalStateException("already started");
        this.taskScheduler = taskScheduler;
    }

    /**
     * Configure a standard root password to use for all newly created worker Linodes.
     *
     * <p>
     * Setting this property allows use of root password authentication with Linode workers that are already
     * in existence when this worker pool starts, assuming they were in fact created with the same root password.
     *
     * <p>
     * If this property is left unset, newly created workers will get a random auto-generated root password.
     *
     * <p>
     * In any case, the root password for a worker, if known, is available via {@link Worker#getRootPassword}.
     *
     * @param standardRootPassword standard root password
     * @throws IllegalStateException if this instance is already {@link #start}ed
     */
    public synchronized void setStandardRootPassword(final String standardRootPassword) {
        if (this.started)
            throw new IllegalStateException("already started");
        this.standardRootPassword = standardRootPassword;
    }

    /**
     * Get the standard set of SSH flags used for all remote executions.
     *
     * @return mutable copy of the standard ssh flags
     */
    public synchronized List<String> getStandardSshFlags() {
        return new ArrayList<>(this.standardSshFlags);
    }

    /**
     * Configure the standard set of SSH flags used for all remote executions.
     *
     * <p>
     * Default flags are: {@code -2xaT --oServerAliveInterval=10 -oServerAliveCountMax=6 -oTCPKeepAlive=yes}.
     *
     * <p>
     * Note these flags are always included first; any additional per-execution flags passed in the {@code sshFlags} parameter
     * to {@link Worker#execute Worker.execute()} are appended to this list.
     *
     * @param standardSshFlags standard ssh flags
     * @throws IllegalStateException if this instance is already {@link #start}ed
     * @throws IllegalArgumentException if {@code standardSshFlags} is null
     */
    public synchronized void setStandardSshFlags(final List<String> standardSshFlags) {
        if (this.started)
            throw new IllegalStateException("already started");
        if (standardSshFlags == null)
            throw new IllegalArgumentException("null standardSshFlags");
        this.standardSshFlags = new ArrayList<>(standardSshFlags);
    }

// Naming

    /**
     * Build Linode name from worker index.
     *
     * @param index worker index
     * @return linode name
     * @throws IllegalArgumentException if {@code index} is invalid
     */
    public String getWorkerName(int index) {
        if (index < 0)
            throw new IllegalArgumentException("index < 0");
        return String.format("%s%d", this.getWorkerNamePrefix(), index);
    }

    /**
     * Parse worker index from Linode name.
     *
     * @param name linode name
     * @return worker index
     * @throws IllegalArgumentException if {@code name} is not a valid worker name for this worker pool
     */
    public synchronized int getWorkerIndex(String name) {
        if (name == null)
            throw new IllegalArgumentException("null name");
        final String namePrefix = this.getWorkerNamePrefix();
        if (!name.startsWith(namePrefix))
            throw new IllegalArgumentException("invalid name");
        final int index;
        try {
            index = Integer.parseInt(name.substring(namePrefix.length()));
            if (!name.equals(this.getWorkerName(index)))
                throw new IllegalArgumentException("non-canonical index format");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid name", e);
        }
        return index;
    }

    /**
     * Get the common Linode name prefix which is derived from the group name.
     *
     * <p>
     * All workers in a worker pool with group name {@code "My-Worker-Pool"} with have a common Linode prefix
     * {@code "My-Worker-Pool-"}. The suffix is the worker's index in the pool.
     *
     * @return worker name prefix
     */
    public String getWorkerNamePrefix() {
        return this.getGroupName() + "-";
    }

    /**
     * Get a Linode API query filter that matches all of this instance's workers.
     *
     * <p>
     * The filter is guaranteed not to produce false negatives, but it may produce false positives.
     * In other words, querying with this filter returns all workers in the pool but also possibly other Lindoes as well.
     *
     * @return worker name filter
     */
    public synchronized Filter getWorkerFilter() {
        final FilterBuilder fb = new FilterBuilder();
        return fb.where(fb.and(fb.equal("group", this.getGroupName()), fb.contains("label", this.getWorkerNamePrefix()))).build();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.getGroupName() + "]";
    }

// Methods

    /**
     * Generate a random password.
     *
     * @return password consisting of {@value #RANDOM_PASSWORD_DIGITS} random hexadecimal digits
     */
    public String generateRandomPassword() {
        final byte[] passwordBytes = new byte[RANDOM_PASSWORD_DIGITS / 2];
        this.random.nextBytes(passwordBytes);
        passwordBytes[0] &= (byte)0x7f;                                     // avoid negative numbers
        return new BigInteger(passwordBytes).toString(16);
    }

    /**
     * Get all workers.
     *
     * @return immutable snapshot of current worker set
     */
    public synchronized Set<Worker> getWorkers() {
        return Collections.unmodifiableSet(new HashSet<>(this.workers));
    }

    /**
     * Add a new worker.
     *
     * <p>
     * This method executes synchronously to create the Linode, blocking on network I/O if necessary.
     *
     * <p>
     * If no {@linkplain #setStandardRootPassword standard root password} is configured, a random root
     * password will be auto-generated via {@link #generateRandomPassword}.
     *
     * @param regionId Linode region ID
     * @param typeId Linode type ID
     * @param imageId Linode image ID
     * @return newly added worker
     * @throws IllegalStateException if this instance is not {@link #start}ed
     * @throws org.springframework.web.client.RestClientException if an error occurs
     */
    public synchronized Worker addWorker(String regionId, String typeId, String imageId) {
        return this.addWorker(regionId, typeId, imageId, -1, -1, null, null);
    }

    /**
     * Add a new worker.
     *
     * <p>
     * This method executes synchronously to create the Linode, blocking on network I/O if necessary.
     *
     * <p>
     * If no {@linkplain #setStandardRootPassword standard root password} is configured, a random root
     * password will be auto-generated via {@link #generateRandomPassword}.
     *
     * @param regionId Linode region ID
     * @param typeId Linode type ID
     * @param imageId Linode image ID or null for none (must be null if {@code backupId} is not -1)
     * @param backupId Linode backup ID or -1 for none (must be -1 if {@code imageId} is non-null)
     * @param stackScriptId StackScript ID or -1 for none
     * @param stackScriptData StackScript variable data or null for none
     * @param authorizedKeys SSH public keys to pre-install, or null for none
     * @return newly added worker
     * @throws IllegalStateException if this instance is not {@link #start}ed
     * @throws org.springframework.web.client.RestClientException if an error occurs
     */
    public synchronized Worker addWorker(String regionId, String typeId, String imageId, int backupId,
      int stackScriptId, Map<String, String> stackScriptData, List<String> authorizedKeys) {

        // Sanity check
        if (!this.started)
            throw new IllegalStateException("not started");
        if (regionId == null)
            throw new IllegalArgumentException("null regionId");
        if (typeId == null)
            throw new IllegalArgumentException("null typeId");
        if (backupId < -1)
            throw new IllegalArgumentException("backupId < 0");
        if (imageId != null && backupId != -1)
            throw new IllegalArgumentException("both imageId and backupId provided");
        if (imageId == null && backupId == -1)
            throw new IllegalArgumentException("one of imageId and backupId must be provided");
        if (stackScriptId < -1)
            throw new IllegalArgumentException("stackScriptId < -1");

        // Identify the first worker index that isn't already in use
        final List<Integer> indexList = new ArrayList<>(this.buildWorkerMap().keySet());
        Collections.sort(indexList);
        int index = 0;
        while (index < indexList.size() && indexList.get(index) == index)
            index++;

        // Get root password
        final String rootPassword = this.standardRootPassword != null ?
          this.standardRootPassword : this.generateRandomPassword();

        // Create the linode
        final CreateLinodeRequest request = new CreateLinodeRequest();
        request.setLabel(this.getWorkerName(index));
        request.setGroup(this.getGroupName());
        request.setRootPassword(rootPassword);
        request.setRegionId(regionId);
        request.setTypeId(typeId);
        request.setImageId(imageId);
        if (backupId != -1)
            request.setBackupId(backupId);
        if (stackScriptId != -1) {
            request.setStackScriptId(stackScriptId);
            if (stackScriptData != null)
                request.setStackScriptData(stackScriptData);
        }
        if (authorizedKeys != null)
            request.setAuthorizedKeys(authorizedKeys.toArray(new String[authorizedKeys.size()]));
        request.setBackupsEnabled(false);
        request.setBooted(true);
        this.log.info("creating new Linode for worker #{}: \"{}\"", index, request.getLabel());
        final Linode linode = this.sender.createLinode(request);
        final Worker worker = new Worker(this, index, linode, rootPassword, this.standardSshFlags, Worker.State.CREATING);
        this.log.info("created Linode#{} as \"{}\" at {}", worker, linode.getId(), worker.getIpAddress());
        this.workers.add(worker);

        // Done
        return worker;
    }

// Lifecycle

    /**
     * Start this instance.
     *
     * <p>
     * Does nothing if already started.
     *
     * @throws IllegalStateException if no {@link LinodeApiRequestSender} is configured
     * @throws IllegalStateException if no {@link AsyncTaskExecutor} is configured
     * @throws IllegalStateException if no {@link TaskScheduler} is configured
     */
    @PostConstruct
    public synchronized void start() {

        // Sanity check
        if (this.started)
            return;
        if (this.sender == null)
            throw new IllegalStateException("no request sender configured");
        if (this.taskExecutor == null)
            throw new IllegalStateException("no AsyncTaskExecutor configured");
        if (this.taskScheduler == null)
            throw new IllegalStateException("no TaskScheduler configured");

        // Perform the first periodic check
        try {
            this.doPeriodicCheck(true);
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted while querying Linode status", e);
        }
        this.log.info("{}: found {} pre-existing worker(s): {}", this, this.workers.size(), this.workers);

        // Start regular periodic checks
        this.periodicCheckFuture = this.taskScheduler.scheduleWithFixedDelay(this::periodicCheck,
          Date.from(Instant.now().plus(CHECK_INTERVAL)), CHECK_INTERVAL.toMillis());

        // Done
        this.started = true;
    }

    /**
     * Stop this instance.
     *
     * <p>
     * This method does not destroy the remaining workers. Use {@link Worker#destroy} to do that prior to invoking this method.
     *
     * <p>
     * After this method is invoked, any remaining workers will have been moved to the {@link Worker.State#INVALID} state.
     */
    @PreDestroy
    public synchronized void stop() {

        // Sanity check
        if (!this.started)
            return;

        // Stop periodic checks
        if (this.periodicCheckFuture != null) {
            this.periodicCheckFuture.cancel(true);
            this.periodicCheckFuture = null;
        }

        // Done
        for (Worker worker : this.workers)
            worker.setState(Worker.State.INVALID);
        this.workers.clear();
        this.started = false;
    }

// Periodic Check

    private void periodicCheck() {
        try {
            this.doPeriodicCheck(false);
        } catch (ThreadDeath t) {
            throw t;
        } catch (Throwable t) {
            this.log.warn(this + ": error during periodic check (ignoring)", t);
        }
    }

    @SuppressWarnings("fallthrough")
    private void doPeriodicCheck(boolean starting) throws InterruptedException {

        // Check state and snapshot the objects we need
        LinodeApiRequestSender sender0;
        AsyncTaskExecutor taskExecutor0;
        final Filter filter;
        synchronized (this) {
            if (!starting && !this.started)                 // we're being shutdown, bail out
                return;
            sender0 = this.sender;
            taskExecutor0 = this.taskExecutor;
            filter = this.getWorkerFilter();
        }

        // Query for worker linodes - while not synchronized
        final List<Linode> linodes = sender0.getLinodes(
          LinodeApiRequestSender.AsyncExecutor.of(taskExecutor0), Integer.MAX_VALUE, filter);

        // Process results
        synchronized (this) {

            // Check state again
            if (!starting && !this.started)                 // we're being shutdown, bail out
                return;

            // Inventory actual Linodes
            final HashMap<Integer, Linode> linodeMap = new HashMap<>();
            for (Linode linode : linodes) {
                final int index = this.getWorkerIndex(linode.getLabel());
                if (index != -1)
                    linodeMap.put(index, linode);
            }

            // First, identify workers with no corresponding Linode, or whose Linode has been replaced
            for (Iterator<Worker> i = this.workers.iterator(); i.hasNext(); ) {
                final Worker worker = i.next();
                final Linode linode = linodeMap.get(worker.getIndex());
                if (linode == null || linode.getId() != worker.getLinodeId()) {
                    this.log.info("{} (Linode#{}) has {}, removing worker and transitioning to {}", worker, worker.getLinodeId(),
                      linode != null ? "been replaced by Linode#" + linode.getId() : "disappeared", Worker.State.INVALID);
                    worker.setState(Worker.State.INVALID);
                    i.remove();
                }
            }

            // Next, check the status of each worker's Linode
            for (Worker worker : this.workers) {
                final Linode linode = linodeMap.get(worker.getIndex());
                switch (worker.getState()) {
                case UNKNOWN:
                    final Worker.State newState = this.getWorkerStateForLinode(linode);
                    this.log.info("initializing {} state to {} based on Linode#{} status {}",
                      worker, newState, worker.getLinodeId(), linode.getStatus());
                    worker.setState(newState);
                    break;
                case CREATING:
                    if (linode.getStatus().equals(Linode.Status.RUNNING)) {
                        if (this.log.isDebugEnabled())
                            this.log.debug("{}: {} now {}, scheduling ssh connectivity check", this, worker, Linode.Status.RUNNING);
                        this.checkSshConnectivityAsync(worker);
                        break;
                    }
                    // FALLTHROUGH
                case RUNNING:
                case DESTROYING:
                    if (!worker.getState().isValidStatus(linode.getStatus())) {
                        this.log.info("{} Linode#{} has invalid status {} for worker state {} transitioning to {}",
                          worker, worker.getLinodeId(), linode.getStatus(), worker.getState(), Worker.State.UNMANAGED);
                        worker.setState(Worker.State.UNMANAGED);
                    }
                    break;
                case UNMANAGED:
                    break;
                default:
                    this.log.error("unexpected state {} for  {}", worker.getState(), worker);
                    break;
                }
            }

            // Next, deal with Linodes for which there is no corresponding worker
            final Map<Integer, Worker> workerMap = this.buildWorkerMap();
            for (Map.Entry<Integer, Linode> entry : linodeMap.entrySet()) {
                final int index = entry.getKey();
                final Linode linode = entry.getValue();
                Worker worker = workerMap.get(index);
                if (worker != null)
                    continue;
                worker = new Worker(this, index, linode, this.standardRootPassword,
                  this.standardSshFlags, this.getWorkerStateForLinode(linode));
                this.log.info("created {} in state {} based on the existence of Linode#{} in status {}",
                  worker, worker.getState(), worker.getLinodeId(), linode.getStatus());
                this.workers.add(worker);
            }

            // Check for stuck Linodes
            final Instant now = Instant.now();
            for (Worker worker : this.workers) {
                final int maxTime;
                switch (worker.getState()) {
                case CREATING:
                    maxTime = this.maxStartupTime;
                    break;
                case DESTROYING:
                    maxTime = this.maxShutdownTime;
                    break;
                default:
                    continue;
                }
                final Instant lastStateChangeTime = worker.getLastStateChangeTime();
                if (lastStateChangeTime.plus(Duration.ofSeconds(maxTime)).isAfter(now))
                    continue;
                final Linode linode = linodeMap.get(worker.getIndex());
                this.log.info("{} Linode#{} still has status {} after {}, reverting to {}", worker, worker.getLinodeId(),
                  linode.getStatus(), Duration.between(lastStateChangeTime, now), Worker.State.UNMANAGED);
                worker.setState(Worker.State.UNMANAGED);
            }

            // Check for worker processes that have exited and clean them up
            for (Worker worker : this.workers) {
                boolean idle = true;
                for (Iterator<Process> i = worker.getProcessSet().iterator(); i.hasNext(); ) {
                    final Process process = i.next();
                    if (process.isAlive()) {
                        idle = false;
                        continue;
                    }
                    if (this.log.isDebugEnabled())
                        this.log.debug("cleaning up {} process {} (exit value {})", worker, process, process.exitValue());
                    i.remove();
                }
                if (idle && worker.getState().equals(Worker.State.RUNNING) && worker.getIdleStartTime() == null)
                    worker.setIdleStartTime(Instant.now());
            }

            // Shutdown workers that have idle timed out, until we reach minWorkers
            int numRemainingWorkers = this.workers.size();
            for (Worker worker : this.workers) {
                if (numRemainingWorkers <= this.minWorkers)
                    break;
                if (!worker.getProcesses().isEmpty() || !worker.getState().equals(Worker.State.RUNNING))
                    continue;
                final Instant idleStartTime = worker.getIdleStartTime();
                if (idleStartTime.plus(Duration.ofSeconds(this.maxIdleTime)).isAfter(now))
                    continue;
                this.log.info("{} has idle timed out after {}, destroying", worker, Duration.between(idleStartTime, now));
                worker.setState(Worker.State.DESTROYING);
                this.destroyAsync(worker);
                numRemainingWorkers--;
            }
        }
    }

// Internal methods

    /**
     * Schedule the specified worker for destruction.
     *
     * @param worker worker to destroy
     * @return true if successful, false if {@code worker} is already in state {@link Worker.State#DESTROYING},
     *  or is in state {@link Worker.State#INVALID}
     */
    protected synchronized boolean destroy(Worker worker) {
        if (!this.started)
            return false;
        if (!this.workers.contains(worker))
            return false;
        switch (worker.getState()) {
        case UNKNOWN:
        case CREATING:
        case RUNNING:
        case UNMANAGED:
            this.log.info("destroying {} currently in state {}", worker, worker.getState());
            worker.setState(Worker.State.DESTROYING);
            this.destroyAsync(worker);
            return true;
        default:
            return false;
        }
    }

    private void destroyAsync(Worker worker) {
        assert Thread.holdsLock(this);
        this.taskExecutor.execute(() -> {
            synchronized (this) {
                if (!this.started)                 // we're being shutdown, bail out
                    return;
                try {
                    this.sender.deleteLinode(worker.getLinodeId());
                } catch (ThreadDeath t) {
                    throw t;
                } catch (Throwable t) {
                    this.log.warn(this + ": error destroying Linode#" + worker.getLinodeId() + " (ignoring)", t);
                }
            }
        });
    }

    private void checkSshConnectivityAsync(Worker worker) {
        assert Thread.holdsLock(this);
        final Instant lastStateChangeTime = worker.getLastStateChangeTime();
        this.taskExecutor.execute(() -> {
            synchronized (this) {
                if (!this.started)                                                      // we're being shutdown, bail out
                    return;
                if (!worker.getState().equals(Worker.State.CREATING)
                  || !worker.getLastStateChangeTime().equals(lastStateChangeTime))
                    return;
            }
            if (this.log.isDebugEnabled())
                this.log.debug("{}: performing ssh connectivity check for {}", this, worker);
            try {
                this.checkSshConnectivity(worker);
            } catch (ThreadDeath t) {
                throw t;
            } catch (Throwable t) {
                if (this.log.isDebugEnabled())
                    this.log.debug("{}: ssh connectivity check failed for {}: {}", this, worker, t.toString());
                return;
            }
            if (this.log.isDebugEnabled())
                this.log.debug("{}: ssh connectivity check for succeeded {}", this, worker);
            synchronized (this) {
                if (!this.started)                                                      // we're being shutdown, bail out
                    return;
                if (!worker.getState().equals(Worker.State.CREATING)
                  || !worker.getLastStateChangeTime().equals(lastStateChangeTime))
                    return;
                this.log.info("{} Linode#{} ssh connectivity check succeeded, transitioning to {}",
                  worker, worker.getLinodeId(), Worker.State.RUNNING);
                worker.setState(Worker.State.RUNNING);
            }
        });
    }

    /**
     * Check for SSH connectivity.
     *
     * <p>
     * Linodes reach the {@link org.dellroad.linode.apiv4.model.Linode.Status#RUNNING} status as soon as they are booted,
     * but the {@code sshd(8)} daemon does not start listening for connections until several seconds later. The purpose of
     * this method is to check whether {@code sshd(8)} is ready.
     *
     * <p>
     * The implementation in {@link WorkerPool} simply attempts to connect to TCP port 22.
     *
     * @param worker worker to check
     * @throws IOException if the check fails
     */
    protected void checkSshConnectivity(Worker worker) throws IOException {
        final InetSocketAddress dest = new InetSocketAddress(worker.getIpAddress(), SSH_PORT);
        try (Socket socket = new Socket()) {
            socket.connect(dest, SSH_CHECK_TIMEOUT_SECONDS * 1000);
        }
    }

    private Worker.State getWorkerStateForLinode(Linode linode) {
        switch (linode.getStatus()) {
        case BOOTING:
        case PROVISIONING:
            return Worker.State.CREATING;
        case RUNNING:
            return Worker.State.RUNNING;
        case SHUTTING_DOWN:
        case DELETING:
            return Worker.State.DESTROYING;
        case OFFLINE:
        case MIGRATING:
        case REBOOTING:
            return Worker.State.UNMANAGED;
        default:
            this.log.error("unexpected status " + linode.getStatus() + " for Linode#" + linode.getId());
            return Worker.State.UNMANAGED;
        }
    }

    private Map<Integer, Worker> buildWorkerMap() {
        assert Thread.holdsLock(this);
        final HashMap<Integer, Worker> workerMap = new HashMap<>();
        for (Worker worker : this.workers)
            workerMap.put(worker.getIndex(), worker);
        return workerMap;
    }
}
