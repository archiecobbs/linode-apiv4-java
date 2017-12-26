
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.workers;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.dellroad.linode.apiv4.model.Linode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One worker in a {@link WorkerPool}.
 *
 * <p><b>Worker Identity</b>
 *
 * <p>
 * {@link Worker} instances are permanently associated with specific Linode instances. In other words, a {@link Worker}'s
 * {@linkplain #getLinodeId Linode ID} never changes. Also, a worker is identified as "the N'th member of worker pool X"
 * entirely by its Linode name (see {@link WorkerPool#getWorkerIndex WorkerPool.getWorkerIndex()}).
 *
 * <p>
 * As it's possible for Linode names to change, and for new Linodes to replace old ones with
 * the same name, it's possible for one {@link Worker} instance to replace another one in a worker pool. In this case,
 * the original {@link Worker} instance transistions to {@link State#INVALID}, leaves the worker pool, and a new
 * {@link Worker} instance is automatically instantiated to replace it.
 */
@ThreadSafe
public class Worker {

    /**
     * Root username ({@value #ROOT_USERNAME}).
     */
    public static final String ROOT_USERNAME = "root";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final WorkerPool pool;
    private final int index;
    private final int linodeId;
    private final String ipAddress;
    private final String rootPassword;
    private final List<String> standardSshFlags;

    @GuardedBy("pool")
    private State state;
    @GuardedBy("pool")
    private Instant lastStateChangeTime = Instant.now();
    @GuardedBy("pool")
    private Instant idleStartTime = Instant.now();
    @GuardedBy("pool")
    private HashSet<Process> processSet = new HashSet<>();

// Constructor

    Worker(WorkerPool pool, int index, Linode linode, String rootPassword, List<String> standardSshFlags, State state) {
        if (pool == null)
            throw new IllegalArgumentException("null pool");
        if (index < 0)
            throw new IllegalArgumentException("invalid index");
        if (linode == null || linode.getId() <= 0)
            throw new IllegalArgumentException("null/invalid linode");
        if (rootPassword == null)
            throw new IllegalArgumentException("null rootPassword");
        if (standardSshFlags == null)
            throw new IllegalArgumentException("null standardSshFlags");
        if (state == null)
            throw new IllegalArgumentException("null state");
        this.pool = pool;
        this.index = index;
        this.linodeId = linode.getId();
        this.ipAddress = linode.getIpv4()[0];
        this.rootPassword = rootPassword;
        this.standardSshFlags = standardSshFlags;
        this.state = state;
    }

// Accessors

    /**
     * Get this worker's unique index in its worker pool.
     *
     * @return worker index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Get the Linode name of this worker.
     *
     * @return linode name
     * @see WorkerPool#getWorkerName WorkerPool.getWorkerName()
     */
    public String getName() {
        return this.pool.getWorkerName(this.index);
    }

    /**
     * Get the {@link WorkerPool} with which this instance is associated.
     *
     * @return worker pool
     */
    public WorkerPool getPool() {
        return this.pool;
    }

    /**
     * Get this worker's Linode ID.
     *
     * @return worker Linode ID
     */
    public int getLinodeId() {
        return this.linodeId;
    }

    /**
     * Get this worker's IP address.
     *
     * @return worker IP address
     */
    public String getIpAddress() {
        return this.ipAddress;
    }

    /**
     * Get this worker's root password, if known.
     *
     * @return worker's root password, or null if this worker already existed when its worker pool was started
     *  and no {@linkplain WorkerPool#setStandardRootPassword standard root password} was configured
     * @see WorkerPool#setStandardRootPassword WorkerPool.setStandardRootPassword()
     */
    public String getRootPassword() {
        return this.rootPassword;
    }

    /**
     * Get this worker's state.
     *
     * @return worker state
     */
    public State getState() {
        synchronized (this.pool) {
            return this.state;
        }
    }

    /**
     * Get the {@link Process}es still running (or possibly just completed) on this worker.
     *
     * @return immutable snapshot of this worker's active processes
     */
    public Set<Process> getProcesses() {
        synchronized (this.pool) {
            return Collections.unmodifiableSet(new HashSet<>(this.processSet));
        }
    }

    /**
     * Force this worker into the {@link State#UNMANAGED} state.
     *
     * <p>
     * This method does nothing in the {@link State#UNMANAGED} and {@link State#INVALID} states.
     *
     * <p>
     * If this worker is in the {@link State#UNKNOWN}, {@link State#CREATING}, {@link State#RUNNING}, or {@link State#DESTROYING}
     * states, it will be immediately transitioned to the {@link State#UNMANAGED} state.
     *
     * @return true if this worker's state was changed, false if already in {@link State#UNMANAGED} or {@link State#INVALID}
     */
    public boolean setUnmanaged() {
        synchronized (this.pool) {
            switch (this.state) {
            case UNKNOWN:
            case CREATING:
            case RUNNING:
            case DESTROYING:
                this.setState(State.UNMANAGED);
                return true;
            default:
                return false;
            }
        }
    }

    /**
     * Reactivate this {@link State#UNMANAGED} worker, transitioning it to the {@link State#UNKNOWN} state.
     *
     * <p>
     * This method does nothing unless this worker is in the {@link State#UNMANAGED} state. If so, it will be immediately
     * transitioned to the {@link State#UNKNOWN} state, and eventually (within several seconds) updated based on the current
     * status of its corresponding Linode instance.
     *
     * @return true if this worker's state was changed, false if this worker is not currently {@link State#UNMANAGED}
     */
    public boolean setManaged() {
        synchronized (this.pool) {
            if (this.state.equals(State.UNMANAGED)) {
                this.setState(State.UNKNOWN);
                return true;
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[name=" + this.getName() + ",state=" + this.state + "]";
    }

// Other Methods

    /**
     * Forcibly destroy this worker.
     *
     * <p>
     * This method does nothing in the {@link State#DESTROYING} and {@link State#INVALID} states.
     *
     * @return true if successful, false if this instance is in state {@link State#DESTROYING} or {@link State#INVALID}
     */
    public boolean destroy() {
        return this.pool.destroy(this);
    }

    /**
     * Execute the specified command on this worker by remote execution over SSH.
     *
     * <p>
     * If successful, the returned {@link Process} will be added to this worker's {@linkplain #getProcesses current process set}.
     *
     * <p>
     * This method invokes the command as {@code root}, using SSH password authentication with this worker's
     * {@linkplain #getRootPassword root password}.
     *
     * <p>
     * <b>Note:</b> This requires that the {@code sshpass(1)} utility exist on the local system. The password is
     * passed securely via file descriptor ({@code -d} flag).
     *
     * <p>
     * <b>Note:</b> For Linodes that already exist when the worker pool is started, there is no way to known the original
     * root password. Therefore, in such cases we require that a {@link WorkerPool#setStandardRootPassword
     * WorkerPool.setStandardRootPassword()} is configured, and assume that it is correct.
     *
     * @param command the command to remotely execute on this worker
     * @return resulting process
     * @throws IOException if process creation fails
     * @throws IllegalStateException if this worker is not currently in state {@link State#RUNNING}
     * @throws IllegalArgumentException if this worker's root password is not known
     */
    public Process execute(List<String> command) throws IOException {
        if (this.rootPassword == null)
            throw new IllegalArgumentException("worker's root password is not known");
        return this.execute(ROOT_USERNAME, this.rootPassword, null, command);
    }

    /**
     * Execute the specified command on this worker by remote execution over SSH.
     *
     * <p>
     * If successful, the returned {@link Process} will be added to this worker's {@linkplain #getProcesses current process set}.
     *
     * <p>
     * This method invokes the command using SSH password authentication with the given username and password.
     *
     * <p>
     * <b>Note:</b> This requires that the {@code sshpass(1)} utility exist on the local system. The password is
     * passed securely via file descriptor ({@code -d} flag).
     *
     * @param username SSH username
     * @param password SSH password
     * @param sshFlags additional flags to {@code ssh(1)}, or null for none
     * @param command the command to remotely execute remotely on this worker
     * @return resulting process
     * @throws IOException if process creation fails
     * @throws IllegalArgumentException if any parameter other than {@code sshFlags} is null
     * @throws IllegalArgumentException if any parameter other than {@code sshFlags} is null
     */
    public Process execute(String username, String password, List<String> sshFlags, List<String> command) throws IOException {
        if (password == null)
            throw new IllegalArgumentException("null password");
        return this.execute(username, command, sshFlags, password);
    }

    /**
     * Execute the specified command on this worker by remote execution over SSH.
     *
     * <p>
     * If successful, the returned {@link Process} will be added to this worker's {@linkplain #getProcesses current process set}.
     *
     * <p>
     * This method invokes the command using SSH public key authentication with the given username and
     * <i>unencrypted</i> secret key file.
     *
     * @param username SSH username
     * @param privateKey file containing unencrypted private key
     * @param sshFlags additional flags to {@code ssh(1)}, or null for none
     * @param command the command to remotely execute on this worker
     * @return resulting process
     * @throws IOException if process creation fails
     * @throws IllegalStateException if this worker is not currently in state {@link State#RUNNING}
     * @throws IllegalArgumentException if any parameter other than {@code sshFlags} is null
     */
    public Process execute(String username, File privateKey, List<String> sshFlags, List<String> command) throws IOException {
        if (privateKey == null)
            throw new IllegalArgumentException("null privateKey");
        final ArrayList<String> newSshFlags = new ArrayList<>();
        newSshFlags.add("-i");
        newSshFlags.add(privateKey.toString());
        if (sshFlags != null)
            newSshFlags.addAll(sshFlags);
        return this.execute(username, command, newSshFlags, null);
    }

    private Process execute(String username, List<String> command, List<String> sshFlags, String password) throws IOException {

        // Sanity check
        if (username == null)
            throw new IllegalArgumentException("null username");
        if (command == null)
            throw new IllegalArgumentException("null command");
        synchronized (this.pool) {
            if (!this.state.equals(State.RUNNING))
                throw new IllegalStateException("worker is in state " + this.state + " != " + State.RUNNING);
        }

        // Build commmand
        final ArrayList<String> commandList = new ArrayList<>();
        if (password != null) {
            commandList.add("sshpass");
            commandList.add("-d0");
        }
        commandList.add("ssh");
        commandList.addAll(this.standardSshFlags);
        if (sshFlags != null)
            commandList.addAll(sshFlags);
        commandList.add(username + "@" + this.ipAddress);
        commandList.addAll(command);

        // Fork process
        if (this.log.isDebugEnabled())
            this.log.debug("{}: forking process: {}", this, commandList);
        final Process process;
        try {
            process = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
        } catch (IOException e) {
            if (this.log.isDebugEnabled())
                this.log.debug("{}: forking process failed: {}", this, e);
            throw e;
        }

        // Supply password to sshpass(1)
        if (password != null)
            this.sendSshPassword(process, password);

        // Add process to process set
        synchronized (this.pool) {

            // Check for state change while we were getting started
            if (!this.state.equals(State.RUNNING)) {
                if (this.log.isDebugEnabled())
                    this.log.debug("{}: state changed to {} while forking, killing nascent {}", this, this.state, process);
                process.destroyForcibly();
                throw new IllegalStateException("worker is in state " + this.state + " != " + State.RUNNING);
            }

            // Add process
            this.processSet.add(process);
            this.idleStartTime = null;
        }

        // Done
        return process;
    }

// Package methods

    void setState(final State state) {
        assert Thread.holdsLock(this.pool);
        if (state == null)
            throw new IllegalArgumentException("null state");
        if (this.state.equals(state))
            return;
        this.log.info("{}: state change {} -> {}", this, this.state, state);
        this.state = state;
        this.lastStateChangeTime = Instant.now();
    }

    Instant getLastStateChangeTime() {
        assert Thread.holdsLock(this.pool);
        return this.lastStateChangeTime;
    }

    HashSet<Process> getProcessSet() {
        assert Thread.holdsLock(this.pool);
        return this.processSet;
    }

    Instant getIdleStartTime() {
        assert Thread.holdsLock(this.pool);
        return this.idleStartTime;
    }

    void setIdleStartTime(final Instant idleStartTime) {
        assert Thread.holdsLock(this.pool);
        this.idleStartTime = idleStartTime;
    }

// Internal helpers

    private void sendSshPassword(Process process, String password) {
        final PrintStream ps = new PrintStream(process.getOutputStream());
        ps.println(password);
        ps.flush();                                 // we intentionally do not close() the PrintStream here
    }

// State

    /**
     * {@link Worker} state.
     */
    public enum State {

        /**
         * The worker's state is unknown.
         *
         * <p>
         * This is the initial state on startup, and after a successful invocation of {@link Worker#setManaged}. During
         * the next periodic check, the corresponding Linode (if any) is examined, and this worker's state is updated
         * based its status:
         * <div style="margin-left: 20px;">
         * <table border="1" cellpadding="3" cellspacing="0" summary="Mapping from Linode status to Worker state">
         * <tr style="bgcolor:#ccffcc">
         *  <th>{@linkplain org.dellroad.linode.apiv4.model.Linode.Status Linode Status}</th>
         *  <th>{@linkplain State Worker State}</th>
         * </tr>
         * <tr>
         *  <td>Linode no longer exists</td>
         *  <td>{@link #INVALID}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#BOOTING}</td>
         *  <td>{@link #CREATING}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#PROVISIONING}</td>
         *  <td>{@link #CREATING}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#RUNNING}</td>
         *  <td>{@link #RUNNING}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#SHUTTING_DOWN}</td>
         *  <td>{@link #DESTROYING}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#DELETING}</td>
         *  <td>{@link #DESTROYING}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#OFFLINE}</td>
         *  <td>{@link #UNMANAGED}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#MIGRATING}</td>
         *  <td>{@link #UNMANAGED}</td>
         * </tr>
         * <tr>
         *  <td>{@link org.dellroad.linode.apiv4.model.Linode.Status#REBOOTING}</td>
         *  <td>{@link #UNMANAGED}</td>
         * </tr>
         * </table>
         * </div>
         */
        UNKNOWN((Linode.Status[])null),

        /**
         * The worker is being created and will transition to {@link #RUNNING} once it is fully up and running
         * and {@linkplain WorkerPool#checkSshConnectivity can be reached} via {@code ssh(1)}.
         *
         * <p>
         * Compatible Linode statuses: {@link org.dellroad.linode.apiv4.model.Linode.Status#BOOTING},
         * {@link org.dellroad.linode.apiv4.model.Linode.Status#PROVISIONING}
         */
        CREATING(Linode.Status.BOOTING, Linode.Status.PROVISIONING),

        /**
         * The worker is up and running normally.
         *
         * <p>
         * This is the only state in which {@link Worker#execute Worker.execute()} can be successfully invoked.
         *
         * <p>
         * Compatible Linode statuses: {@link org.dellroad.linode.apiv4.model.Linode.Status#RUNNING}
         */
        RUNNING(Linode.Status.RUNNING),

        /**
         * The worker has been removed from the worker pool and is being shutdown.
         *
         * <p>
         * In this state, the worker still exists, but does not count as a member of the worker pool.
         * It is expected that it will soon disappear.
         *
         * <p>
         * Compatible Linode statuses: {@link org.dellroad.linode.apiv4.model.Linode.Status#RUNNING},
         * {@link org.dellroad.linode.apiv4.model.Linode.Status#SHUTTING_DOWN},
         * {@link org.dellroad.linode.apiv4.model.Linode.Status#DELETING}
         */
        DESTROYING(Linode.Status.RUNNING, Linode.Status.SHUTTING_DOWN, Linode.Status.DELETING),

        /**
         * The worker did something unexpected, or was {@linkplain Worker#setUnmanaged forcibly unmanaged},
         * and is now "off limits".
         *
         * <p>
         * In this state, the worker still exists and counts as a member of the worker pool, but will no longer
         * be manipulated in any way. {@link Worker}s in this state may be reactivated via {@link Worker#setManaged}.
         *
         * <p>
         * Compatible Linode statuses: Any (though worker must exist)
         */
        UNMANAGED((Linode.Status[])null),

        /**
         * This worker is no longer valid, e.g., because it no longer exists or has been replaced in the worker pool.
         *
         * <p>
         * {@link Worker}s in this state are no longer in the worker pool; they do appear in {@link WorkerPool#getWorkers}.
         *
         * <p>
         * Compatible Linode statuses: Any
         */
        INVALID((Linode.Status[])null);

        private final Linode.Status[] validStatuses;

        State(Linode.Status... validStatuses) {
            this.validStatuses = validStatuses;
        }

        /**
         * Determine whether the given Linode status is valid for {@link Worker}s in this state.
         *
         * @param status Linode status
         * @return true if status is valid, otherwise false
         * @throws IllegalArgumentException if {@code status} is null
         */
        public boolean isValidStatus(Linode.Status status) {
            if (status == null)
                throw new IllegalArgumentException("null status");
            return this.validStatuses == null || Arrays.asList(this.validStatuses).contains(status);
        }
    }
}
