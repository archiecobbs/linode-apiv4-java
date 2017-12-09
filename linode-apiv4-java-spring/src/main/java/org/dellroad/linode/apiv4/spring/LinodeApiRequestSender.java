
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.http.NoHttpResponseException;
import org.dellroad.linode.apiv4.Constants;
import org.dellroad.linode.apiv4.filter.Filter;
import org.dellroad.linode.apiv4.model.BackupInfo;
import org.dellroad.linode.apiv4.model.Config;
import org.dellroad.linode.apiv4.model.Configs;
import org.dellroad.linode.apiv4.model.Disk;
import org.dellroad.linode.apiv4.model.Disks;
import org.dellroad.linode.apiv4.model.Distribution;
import org.dellroad.linode.apiv4.model.Distributions;
import org.dellroad.linode.apiv4.model.IP;
import org.dellroad.linode.apiv4.model.IPInfo;
import org.dellroad.linode.apiv4.model.IPv4;
import org.dellroad.linode.apiv4.model.Image;
import org.dellroad.linode.apiv4.model.Images;
import org.dellroad.linode.apiv4.model.Kernel;
import org.dellroad.linode.apiv4.model.Kernels;
import org.dellroad.linode.apiv4.model.Linode;
import org.dellroad.linode.apiv4.model.Linodes;
import org.dellroad.linode.apiv4.model.Paginated;
import org.dellroad.linode.apiv4.model.Region;
import org.dellroad.linode.apiv4.model.Regions;
import org.dellroad.linode.apiv4.model.StackScript;
import org.dellroad.linode.apiv4.model.StackScripts;
import org.dellroad.linode.apiv4.model.Stats;
import org.dellroad.linode.apiv4.model.Type;
import org.dellroad.linode.apiv4.model.Types;
import org.dellroad.linode.apiv4.model.Volume;
import org.dellroad.linode.apiv4.model.Volumes;
import org.dellroad.linode.apiv4.request.CloneLinodeRequest;
import org.dellroad.linode.apiv4.request.CreateConfigRequest;
import org.dellroad.linode.apiv4.request.CreateDiskRequest;
import org.dellroad.linode.apiv4.request.CreateLinodeRequest;
import org.dellroad.linode.apiv4.request.CreateVolumeRequest;
import org.dellroad.linode.apiv4.request.ImagizeDiskRequest;
import org.dellroad.linode.apiv4.request.RescueLinodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Sender for Linode REST APIv4 requests.
 */
public class LinodeApiRequestSender implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private URI baseURI = Constants.BASE_URI;
    private RestTemplate restTemplate;

// Lifecycle

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.restTemplate == null)
            throw new Exception("no restTemplate configured");
    }

// Properties

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public URI getBaseURI() {
        return this.baseURI;
    }
    public void setBaseURI(URI baseURI) {
        if (baseURI == null)
            throw new IllegalArgumentException("null baseURI");
        this.baseURI = baseURI;
    }

// Linodes

    /**
     * Get all Linode instances.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all Linodes
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Linode> getAllLinodes(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Linodes.class, executor, filter, "linode/instances");
    }

    /**
     * Get all Linode instances.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all Linodes
     * @throws RestClientException if an error occurs
     */
    public List<Linode> getAllLinodes(Filter filter) {
        return this.getAll(Linodes.class, filter, "linode/instances");
    }

    /**
     * Get one page of Linode instances.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of Linodes
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Linodes getLinodes(Filter filter, int page) {
        return this.getPaginated(Linodes.class, filter, page, "linode/instances");
    }

    /**
     * Get a particular Linode instance.
     *
     * @param linodeId Linode ID
     * @return specified Linode
     * @throws RestClientException if an error occurs
     */
    public Linode getLinode(int linodeId) {
        return this.get(Linode.class, "linode/instances/{id}", linodeId);
    }

    /**
     * Create a new Linode instance.
     *
     * @param request create info
     * @return new Linode
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     */
    public Linode createLinode(CreateLinodeRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        return this.postFor(Linode.class, request, "linode/instances");
    }

    /**
     * Update a Linode instance.
     *
     * @param request update info
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     * @throws IllegalArgumentException if {@code request} has invalid ID
     */
    public void updateLinode(Linode request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        if (request.getId() <= 0)
            throw new IllegalArgumentException("invalid linode ID");
        this.put(request, "linode/instances/{id}", request.getId());
    }

    /**
     * Delete a Linode instance.
     *
     * @param linodeId linode ID
     * @throws RestClientException if an error occurs
     */
    public void deleteLinode(int linodeId) {
        this.delete("linode/instances/{id}", linodeId);
    }

// Linodes: Boot

    /**
     * Boot a Linode instance.
     *
     * @param linodeId Linode ID
     * @throws RestClientException if an error occurs
     */
    public void bootLinode(int linodeId) {
        this.boot(linodeId, -1);
    }

    /**
     * Boot a Linode instance.
     *
     * @param linodeId Linode ID
     * @param configId Configuration ID, or -1 for none
     * @throws RestClientException if an error occurs
     */
    public void boot(int linodeId, int configId) {
        final Query query = configId != -1 ? new Query("config_id", configId) : new Query();
        this.post(query, "linode/instances/{id}/boot", linodeId);
    }

// Linodes: Clone

    /**
     * Clone a Linode instance.
     *
     * @param linodeId Linode ID of the instance to clone
     * @param request clone request
     * @return new Linode
     * @throws RestClientException if an error occurs
     */
    public Linode cloneLinode(int linodeId, CloneLinodeRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        return this.postFor(Linode.class, request, "linode/instances/{id}/clone", linodeId);
    }

// Linodes: KVMify

    /**
     * Convert a Linode instance to KVM.
     *
     * @param linodeId Linode ID of the instance to KVM'ify
     * @throws RestClientException if an error occurs
     */
    public void kvmifyLinode(int linodeId) {
        this.post("", "linode/instances/{id}/kvmify", linodeId);
    }

// Linodes: Mutate

    /**
     * Upgrade a Linode to its next generation.
     *
     * @param linodeId Linode ID of the instance to upgrade
     * @throws RestClientException if an error occurs
     */
    public void mutateLinode(int linodeId) {
        this.post("", "linode/instances/{id}/mutate", linodeId);
    }

// Linodes: Reboot

    /**
     * Reboot a Linode instance.
     *
     * @param linodeId Linode ID
     * @throws RestClientException if an error occurs
     */
    public void rebootLinode(int linodeId) {
        this.rebootLinode(linodeId, -1);
    }

    /**
     * Reboot a Linode instance.
     *
     * @param linodeId Linode ID
     * @param configId Configuration ID, or -1 for none
     * @throws RestClientException if an error occurs
     */
    public void rebootLinode(int linodeId, int configId) {
        final Query query = configId != -1 ? new Query("config_id", configId) : new Query();
        this.post(query, "linode/instances/{id}/reboot", linodeId);
    }

// Linodes: Rebuild - TODO

// Linodes: Rescue

    /**
     * Reboot a Linode instance in rescue mode.
     *
     * @param linodeId Linode ID of the instance to clone
     * @param request rescue request
     * @throws RestClientException if an error occurs
     */
    public void rescueLinode(int linodeId, RescueLinodeRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        this.post(request, "linode/instances/{id}/rescue", linodeId);
    }

// Linodes: Resize

    /**
     * Resize a Linode instance.
     *
     * @param linodeId Linode ID
     * @param typeId new type ID
     * @throws RestClientException if an error occurs
     */
    public void resizeLinode(int linodeId, String typeId) {
        this.post(new Query("type", typeId), "linode/instances/{id}/resize", linodeId);
    }

// Linodes: Shutdown

    /**
     * Shutdown a Linode.
     *
     * @param linodeId Linode ID of the instance
     * @throws RestClientException if an error occurs
     */
    public void shutdownLinode(int linodeId) {
        this.post("", "linode/instances/{id}/shutdown", linodeId);
    }

// Linodes: Volumes

    /**
     * Get all volumes attached to a Linode instance.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @param linodeId Linode ID
     * @return all attached volumes
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Volume> getAllLinodeVolumes(AsyncTaskExecutor executor, Filter filter, int linodeId) throws InterruptedException {
        return this.getAll(Volumes.class, executor, filter, "linode/instances/{id}/volumes", linodeId);
    }

    /**
     * Get all volumes attached to a Linode instance.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @param linodeId Linode ID
     * @return all attached volumes
     * @throws RestClientException if an error occurs
     */
    public List<Volume> getAllLinodeVolumes(Filter filter, int linodeId) {
        return this.getAll(Volumes.class, filter, "linode/instances/{id}/volumes", linodeId);
    }

    /**
     * Get one page of the volumes attached to a Linode instance.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @param linodeId Linode ID
     * @return one page of volumes
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Volumes getLinodeVolumes(Filter filter, int page, int linodeId) {
        return this.getPaginated(Volumes.class, filter, page, "linode/instances/{id}/volumes", linodeId);
    }

// Linodes: Backups

    /**
     * Get backup info about a Linode instance.
     *
     * @param linodeId Linode ID
     * @return backup info for instance
     * @throws RestClientException if an error occurs
     */
    public BackupInfo getLinodeBackupInfo(int linodeId) {
        return this.get(BackupInfo.class, "linode/instances/{id}/backups", linodeId);
    }

    // TODO: restore backup
    // TODO: cancel backup
    // TODO: enable backup

// Linodes: Configs

    /**
     * Get all configs associated with a Linode instance.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @param linodeId Linode ID
     * @return all associated configs
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Config> getAllLinodeConfigs(AsyncTaskExecutor executor, Filter filter, int linodeId) throws InterruptedException {
        return this.getAll(Configs.class, executor, filter, "linode/instances/{id}/configs", linodeId);
    }

    /**
     * Get all configs associated with a Linode instance.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @param linodeId Linode ID
     * @return all associated configs
     * @throws RestClientException if an error occurs
     */
    public List<Config> getAllLinodeConfigs(Filter filter, int linodeId) {
        return this.getAll(Configs.class, filter, "linode/instances/{id}/configs", linodeId);
    }

    /**
     * Get one page of the configurations associated with a Linode instance.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @param linodeId Linode ID
     * @return one page of configs for instance
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Configs getLinodeConfigs(Filter filter, int page, int linodeId) {
        return this.getPaginated(Configs.class, filter, page, "linode/instances/{id}/configs", linodeId);
    }

    /**
     * Get a particular Linode config.
     *
     * @param linodeId Linode ID
     * @param configId Config ID
     * @return requested config
     * @throws RestClientException if an error occurs
     */
    public Config getLinodeConfig(int linodeId, int configId) {
        return this.get(Config.class, "linode/instances/{id}/configs/{cid}", linodeId, configId);
    }

    /**
     * Create a new Linode config.
     *
     * @param linodeId Linode ID
     * @param request config info
     * @return new config
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     */
    public Config createLinodeConfig(int linodeId, CreateConfigRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        return this.postFor(Config.class, request, "linode/instances/{id}/configs", linodeId);
    }

    /**
     * Update a Linode config.
     *
     * @param linodeId Linode ID
     * @param request update info (with config ID set)
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     */
    public void updateLinodeConfig(int linodeId, Config request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        this.put(request, "linode/instances/{id}/config/{cid}", linodeId, request.getId());
    }

    /**
     * Delete a Linode config.
     *
     * @param linodeId linode ID
     * @param configId config ID
     * @throws RestClientException if an error occurs
     */
    public void deleteLinodeConfig(int linodeId, int configId) {
        this.delete("linode/instances/{id}/config/{cid}", linodeId, configId);
    }

// Linodes: Disks

    /**
     * Get all disks associated with a Linode instance.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @param linodeId Linode ID
     * @return all associated disks
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Disk> getAllLinodeDisks(AsyncTaskExecutor executor, Filter filter, int linodeId) throws InterruptedException {
        return this.getAll(Disks.class, executor, filter, "linode/instances/{id}/disks", linodeId);
    }

    /**
     * Get all disks associated with a Linode instance.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @param linodeId Linode ID
     * @return all associated disks
     * @throws RestClientException if an error occurs
     */
    public List<Disk> getAllLinodeDisks(Filter filter, int linodeId) {
        return this.getAll(Disks.class, filter, "linode/instances/{id}/disks", linodeId);
    }

    /**
     * Get one page of the disks associated with a Linode instance.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @param linodeId Linode ID
     * @return linode disks info
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Disks getLinodeDisks(Filter filter, int page, int linodeId) {
        return this.getPaginated(Disks.class, filter, page, "linode/instances/{id}/disks", linodeId);
    }

    /**
     * Get the specified disk.
     *
     * @param linodeId Linode ID
     * @param diskId disk ID
     * @return linode disk info
     * @throws RestClientException if an error occurs
     */
    public Disk getLinodeDisk(int linodeId, int diskId) {
        return this.get(Disk.class, "linode/instances/{id}/disks/{did}", linodeId, diskId);
    }

    /**
     * Create a new Linode disk.
     *
     * @param linodeId Linode ID
     * @param request disk info
     * @return new disk
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     */
    public Disk createLinodeDisk(int linodeId, CreateDiskRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        return this.postFor(Disk.class, request, "linode/instances/{id}/disks", linodeId);
    }

    /**
     * Update a Linode disk.
     *
     * @param linodeId Linode ID
     * @param diskId disk ID
     * @param label new label
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code label} is null
     */
    public void updateLinodeDisk(int linodeId, int diskId, String label) {
        if (label == null)
            throw new IllegalArgumentException("null label");
        this.put(new Query("label", label), "linode/instances/{id}/disks/{did}", linodeId, diskId);
    }

    /**
     * Delete a Linode disk.
     *
     * @param linodeId Linode ID
     * @param diskId disk ID
     * @throws RestClientException if an error occurs
     */
    public void deleteLinodeDisk(int linodeId, int diskId) {
        this.delete("linode/instances/{id}/disks/{cid}", linodeId, diskId);
    }

    /**
     * Create an image of a disk.
     *
     * @param linodeId Linode ID
     * @param diskId disk ID
     * @param request image info
     * @return newly created image
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     */
    public Image updateLinodeDiskPassword(int linodeId, int diskId, ImagizeDiskRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        return this.postFor(Image.class, request, "linode/instances/{id}/disks/{did}/imagize", linodeId, diskId);
    }

    /**
     * Reset root password on a Linode disk.
     *
     * @param linodeId Linode ID
     * @param diskId disk ID
     * @param password new root password
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code password} is null
     */
    public void updateLinodeDiskPassword(int linodeId, int diskId, String password) {
        if (password == null)
            throw new IllegalArgumentException("null password");
        this.post(new Query("password", password), "linode/instances/{id}/disks/{did}/password", linodeId, diskId);
    }

    /**
     * Resize a Linode disk.
     *
     * @param linodeId Linode ID
     * @param diskId disk ID
     * @param size new size in MB
     * @throws RestClientException if an error occurs
     */
    public void updateLinodeDisk(int linodeId, int diskId, int size) {
        this.put(new Query("size", size), "linode/instances/{id}/disks/{did}/resize", linodeId, diskId);
    }

// Distributions

    /**
     * Get all Linux distributions.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all distributions
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Distribution> getAllDistributions(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Distributions.class, executor, filter, "linode/distributions");
    }

    /**
     * Get all Linux distributions.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all distributions
     * @throws RestClientException if an error occurs
     */
    public List<Distribution> getAllDistributions(Filter filter) {
        return this.getAll(Distributions.class, filter, "linode/distributions");
    }

    /**
     * Get one page of Linux distributions.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of distributions
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Distributions getDistributions(Filter filter, int page) {
        return this.getPaginated(Distributions.class, filter, page, "linode/distributions");
    }

    /**
     * Get a specific Linux distribution.
     *
     * @param distId distribution ID
     * @return requested distribution
     * @throws RestClientException if an error occurs
     */
    public Distribution getDistribution(int distId) {
        return this.get(Distribution.class, "linode/distributions/{did}", distId);
    }

// IPs

    /**
     * Get IP info.
     *
     * @param linodeId Linode ID
     * @return IP info for linode
     * @throws RestClientException if an error occurs
     */
    public IPInfo getIPInfo(int linodeId) {
        return this.get(IPInfo.class, "linode/instances/{id}/ips", linodeId);
    }

    /**
     * Allocate a new IPv4 address.
     *
     * @param linodeId Linode ID
     * @param type type of address
     * @return newly allocated address
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code type} is null
     */
    public IPInfo allocateIP(int linodeId, IP.Type type) {
        if (type == null)
            throw new IllegalArgumentException("null type");
        return this.postFor(IPInfo.class, new Query("type", type), "linode/instances/{id}/ips", linodeId);
    }

    /**
     * Get info about a specific IPv4 address.
     *
     * @param linodeId Linode ID
     * @param address IP address
     * @return address info
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code address} is null
     */
    public IPv4 getIP(int linodeId, String address) {
        if (address == null)
            throw new IllegalArgumentException("null address");
        return this.get(IPv4.class, "linode/instances/{id}/ips/{addr}", linodeId, address);
    }

    /**
     * Update info about a specific IPv4 address.
     *
     * @param linodeId Linode ID
     * @param address IP address
     * @param info updated info
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code info} is null
     */
    public void updateIP(int linodeId, String address, IP info) {
        if (info == null)
            throw new IllegalArgumentException("null info");
        this.put(info, "linode/instances/{id}/ips/{addr}", linodeId, address);
    }

    /**
     * Delete a specific IPv4 address.
     *
     * @param linodeId Linode ID
     * @param address IP address
     * @throws RestClientException if an error occurs
     */
    public void deleteIP(int linodeId, String address) {
        this.delete("linode/instances/{id}/ips/{addr}", linodeId, address);
    }

// Kernels

    /**
     * Get all kernels.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all kernels
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Kernel> getAllKernels(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Kernels.class, executor, filter, "linode/kernels");
    }

    /**
     * Get all kernels.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all kernels
     * @throws RestClientException if an error occurs
     */
    public List<Kernel> getAllKernels(Filter filter) {
        return this.getAll(Kernels.class, filter, "linode/kernels");
    }

    /**
     * Get one page of kernels.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of kernel
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Kernels getKernels(Filter filter, int page) {
        return this.getPaginated(Kernels.class, filter, page, "linode/kernels");
    }

    /**
     * Get a specific kernel.
     *
     * @param kernelId kernel ID
     * @return specified kernel
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code kernelId} is null
     */
    public Kernel getKernel(String kernelId) {
        if (kernelId == null)
            throw new IllegalArgumentException("null kernelId");
        return this.get(Kernel.class, "linode/kernels/{did}", kernelId);
    }

// StackScripts

    /**
     * Get all StackScripts.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all StackScripts
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<StackScript> getAllStackScripts(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(StackScripts.class, executor, filter, "linode/stackscripts");
    }

    /**
     * Get all StackScripts.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all StackScripts
     * @throws RestClientException if an error occurs
     */
    public List<StackScript> getAllStackScripts(Filter filter) {
        return this.getAll(StackScripts.class, filter, "linode/stackscripts");
    }

    /**
     * Get one page of StackScripts.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of StackScripts
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public StackScripts getStackScripts(Filter filter, int page) {
        return this.getPaginated(StackScripts.class, filter, page, "linode/stackscripts");
    }

    /**
     * Get a specific StackScript.
     *
     * @param scriptId script ID
     * @return specified StackScript
     * @throws RestClientException if an error occurs
     */
    public StackScript getStackScript(int scriptId) {
        return this.get(StackScript.class, "linode/stackscripts/{sid}", scriptId);
    }

// Stats

    /**
     * Get stats for the past 24 hours.
     *
     * @param linodeId Linode ID
     * @return instance stats for the past 24 hours
     * @throws RestClientException if an error occurs
     */
    public Stats getStats(int linodeId) {
        return this.get(Stats.class, "linode/instances/{id}/stats", linodeId);
    }

    /**
     * Get stats for the specified month.
     *
     * @param linodeId Linode ID
     * @param month month
     * @return instance stats for the specified month
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code month} is null
     */
    public Stats getStats(int linodeId, YearMonth month) {
        if (month == null)
            throw new IllegalArgumentException("null month");
        return this.get(Stats.class,
          "linode/instances/{id}/stats/{year}/{month}", linodeId, month.getYear(), month.getMonthValue());
    }

// Types

    /**
     * Get all Linode types.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all types
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Type> getAllTypes(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Types.class, executor, filter, "linode/types");
    }

    /**
     * Get all Linode types.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all types
     * @throws RestClientException if an error occurs
     */
    public List<Type> getAllTypes(Filter filter) {
        return this.getAll(Types.class, filter, "linode/types");
    }

    /**
     * Get one page of Linode types.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of instance types
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Types getTypes(Filter filter, int page) {
        return this.getPaginated(Types.class, filter, page, "linode/types");
    }

    /**
     * Get a specific Linode type.
     *
     * @param typeId type ID
     * @return specified instance type
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code typeId} is null
     */
    public Type getType(String typeId) {
        if (typeId == null)
            throw new IllegalArgumentException("null typeId");
        return this.get(Type.class, "linode/types/{tid}", typeId);
    }

// Volumes

    /**
     * Get all volumes.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all volumes
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Volume> getAllVolumes(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Volumes.class, executor, filter, "linode/volumes");
    }

    /**
     * Get all volumes.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all volumes
     * @throws RestClientException if an error occurs
     */
    public List<Volume> getAllVolumes(Filter filter) {
        return this.getAll(Volumes.class, filter, "linode/volumes");
    }

    /**
     * Get one page of volumes.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of volumes
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Volumes getVolumes(Filter filter, int page) {
        return this.getPaginated(Volumes.class, filter, page, "linode/volumes");
    }

    /**
     * Create a new volume.
     *
     * @param request volume info
     * @return new volume
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code request} is null
     */
    public Volume createVolume(CreateVolumeRequest request) {
        if (request == null)
            throw new IllegalArgumentException("null request");
        return this.postFor(Volume.class, request, "linode/volumes");
    }

    /**
     * Get a specific volume.
     *
     * @param volumeId volume ID
     * @return specified volume
     * @throws RestClientException if an error occurs
     */
    public Volume getVolume(int volumeId) {
        return this.get(Volume.class, "linode/volumes/{vid}", volumeId);
    }

    /**
     * Delete a specific volume.
     *
     * @param volumeId volume ID
     * @throws RestClientException if an error occurs
     */
    public void deleteVolume(int volumeId) {
        this.delete("linode/volumes/{vid}", volumeId);
    }

    /**
     * Attach a volume to a Linode.
     *
     * @param linodeId Linode ID
     * @param volumeId volume ID
     * @param configId config ID (optional)
     * @throws RestClientException if an error occurs
     */
    public void attachVolume(int linodeId, int volumeId, Integer configId) {
        this.post(new Query("linode_id", linodeId, "config_id", configId), "linode/volumes/{vid}/attach", volumeId);
    }

    /**
     * Clone a volume.
     *
     * @param volumeId volume ID
     * @param label unique label for new volume
     * @return new volume
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code label} is null
     */
    public Volume cloneVolume(int volumeId, String label) {
        return this.postFor(Volume.class, new Query("label", label), "linode/volumes/{vid}", volumeId);
    }

// Domains

    // TODO - domains

// Longview

    // TODO - longview

// NodeBalancers

    // TODO - node balancers

// Networking

    // TODO - networking

// Regions

    /**
     * Get all Linode regions.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all regions
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Region> getAllRegions(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Regions.class, executor, filter, "regions");
    }

    /**
     * Get all Linode regions.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all regions
     * @throws RestClientException if an error occurs
     */
    public List<Region> getAllRegions(Filter filter) {
        return this.getAll(Regions.class, filter, "linode/regions");
    }

    /**
     * Get one page of Linode regions.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of regions
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Regions getRegions(Filter filter, int page) {
        return this.getPaginated(Regions.class, filter, page, "regions");
    }

    /**
     * Get a Linode region.
     *
     * @param regionId region ID
     * @return specified region
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code regionId} is null
     */
    public Region getRegion(String regionId) {
        if (regionId == null)
            throw new IllegalArgumentException("null regionId");
        return this.get(Region.class, "regions/{id}", regionId);
    }

// Support

    // TODO - support

// Account

    // TODO - account

// Profile

    // TODO - profile

// Images

    /**
     * Get all images.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param executor executor for loading pages
     * @param filter filter for returned values, or null for no filter
     * @return all images
     * @throws RestClientException if an error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    public List<Image> getAllImages(AsyncTaskExecutor executor, Filter filter) throws InterruptedException {
        return this.getAll(Images.class, executor, filter, "images");
    }

    /**
     * Get all images.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param filter filter for returned values, or null for no filter
     * @return all images
     * @throws RestClientException if an error occurs
     */
    public List<Image> getAllImages(Filter filter) {
        return this.getAll(Images.class, filter, "linode/images");
    }

    /**
     * Get one page of images.
     *
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @return one page of images
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     */
    public Images getImages(Filter filter, int page) {
        return this.getPaginated(Images.class, filter, page, "images");
    }

    /**
     * Get an image.
     *
     * @param imageId image ID
     * @return specified image
     * @throws RestClientException if an error occurs
     */
    public Image getImage(int imageId) {
        return this.get(Image.class, "images/{id}", imageId);
    }

    /**
     * Delete an image.
     *
     * @param imageId image ID
     * @throws RestClientException if an error occurs
     */
    public void deleteImage(int imageId) {
        this.delete("linode/images/{id}", imageId);
    }

// Internal methods

    /**
     * Query a resource via GET.
     *
     * @param responseType response type
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @param <R> response type
     * @return response from query
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     * @throws IllegalArgumentException if {@code responseType} or {@code pathTemplate} is null
     */
    protected <R> R get(Class<R> responseType, String pathTemplate, Object... templateParameters) {

        // Sanity check
        if (responseType == null)
            throw new IllegalArgumentException("null responseType");
        if (pathTemplate == null)
            throw new IllegalArgumentException("null pathTemplate");

        // Perform query
        return this.query(rt -> rt.getForObject(this.buildTemplateURL(pathTemplate), responseType, templateParameters));
    }

    /**
     * POST to a resource.
     *
     * @param request request object
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if any parameter is null
     */
    protected void post(Object request, String pathTemplate, Object... templateParameters) {
        this.postFor(String.class, request, pathTemplate, templateParameters);
    }

    /**
     * POST to a resource expecting response.
     *
     * @param responseType response type
     * @param request request object
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @param <R> response type
     * @return returned response
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if any parameter is null
     */
    protected <R> R postFor(Class<R> responseType, Object request, String pathTemplate, Object... templateParameters) {

        // Sanity check
        if (responseType == null)
            throw new IllegalArgumentException("null responseType");
        if (request == null)
            throw new IllegalArgumentException("null request");
        if (pathTemplate == null)
            throw new IllegalArgumentException("null pathTemplate");

        // Perform query
        return this.query(rt -> rt.postForObject(this.buildTemplateURL(pathTemplate), request, responseType, templateParameters));
    }

    /**
     * PUT a resource.
     *
     * @param request request object
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if any parameter is null
     */
    protected void put(Object request, String pathTemplate, Object... templateParameters) {

        // Sanity check
        if (request == null)
            throw new IllegalArgumentException("null request");
        if (pathTemplate == null)
            throw new IllegalArgumentException("null pathTemplate");

        // Perform query
        this.submit(rt -> rt.put(this.buildTemplateURL(pathTemplate), request, templateParameters));
    }

    /**
     * DELETE a resource.
     *
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if any parameter is null
     */
    protected void delete(String pathTemplate, Object... templateParameters) {

        // Sanity check
        if (pathTemplate == null)
            throw new IllegalArgumentException("null pathTemplate");

        // Perform query
        this.submit(rt -> rt.delete(this.buildTemplateURL(pathTemplate), templateParameters));
    }

    /**
     * Get all instances of the specified type, by querying for and aggreating however many pages there are.
     *
     * <p>
     * Pages are loaded synchronously and sequentially.
     *
     * @param responseType paginated response type
     * @param filter filter for returned values, or null for no filter
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @param <T> item type
     * @param <P> paginated response type
     * @return combined results from all pages
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if any parameter is null
     */
    protected <T, P extends Paginated<T>> List<T> getAll(Class<P> responseType,
      Filter filter, String pathTemplate, Object... templateParameters) {
        try {
            return this.getAll(responseType, null, filter, pathTemplate, templateParameters);
        } catch (InterruptedException e) {
            throw new RuntimeException("unexpected exception", e);
        }
    }

    /**
     * Get all instances of the specified type, by querying for and aggreating however many pages there are.
     *
     * <p>
     * Pages after the first are loaded asynchronously using the given {@link AsyncTaskExecutor}, if any;
     * if {@code executor} is null, pages are loaded synchronously and sequentially.
     *
     * @param responseType paginated response type
     * @param executor executor loading pages
     * @param filter filter for returned values, or null for no filter
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @param <T> item type
     * @param <P> paginated response type
     * @return combined results from all pages
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code responseType}, {@code pathTemplate}, or {@code templateParameters} is null
     * @throws InterruptedException if the current thread is interrupted while waiting for {@code executor}
     */
    protected <T, P extends Paginated<T>> List<T> getAll(Class<P> responseType,
      AsyncTaskExecutor executor, Filter filter, String pathTemplate, Object... templateParameters) throws InterruptedException {

        // Initialize list
        final ArrayList<T> list = new ArrayList<>();

        // Get first page
        final P firstPage = this.getPaginated(responseType, filter, 1, pathTemplate, templateParameters);
        list.addAll(firstPage.getData());

        // Get remaining pages synchonously if no executor provided
        if (executor == null) {
            for (int pageNum = 2; pageNum <= firstPage.getPages(); pageNum++)
                list.addAll(this.getPaginated(responseType, filter, pageNum, pathTemplate, templateParameters).getData());
            return list;
        }

        // Get remaining pages asynchonously using executor
        final ArrayList<Future<P>> futureList = new ArrayList<>(firstPage.getPages() - 1);
        for (int pageNum = 2; pageNum <= firstPage.getPages(); pageNum++) {
            final int pageNum0 = pageNum;
            futureList.add(executor.submit(() ->
              this.getPaginated(responseType, filter, pageNum0, pathTemplate, templateParameters)));
        }
        for (Future<P> future : futureList) {
            try {
                list.addAll(future.get().getData());
            } catch (ExecutionException e) {
                throw new RestClientException("error in asynchronous page load", e.getCause());
            }
        }

        // Done
        return list;
    }

    /**
     * Query a paginated resource via GET.
     *
     * @param responseType paginated response type
     * @param filter filter for returned values, or null for no filter
     * @param page page number
     * @param pathTemplate resource URI path (relative)
     * @param templateParameters parameter values for parameters in {@code pathTemplate}
     * @param <T> item type
     * @param <P> paginated response type
     * @return response from query
     * @throws RestClientException if an error occurs
     * @throws IllegalArgumentException if {@code page} is less than one
     * @throws IllegalArgumentException if {@code responseType}, {@code pathTemplate}, or {@code templateParameters} is null
     */
    protected <T, P extends Paginated<T>> P getPaginated(Class<P> responseType, Filter filter,
      int page, String pathTemplate, Object... templateParameters) {

        // Sanity check
        if (responseType == null)
            throw new IllegalArgumentException("null responseType");
        if (pathTemplate == null)
            throw new IllegalArgumentException("null pathTemplate");
        if (templateParameters == null)
            throw new IllegalArgumentException("null templateParameters");
        if (page < 1)
            throw new IllegalArgumentException("page < 1");

        // Perform query
        return this.query(rt -> {
            final String url = this.buildTemplateURL(pathTemplate, "page", "" + page);
            final LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            if (filter != null) {
                try {
                    headers.add(Constants.FILTER_HEADER, this.toJson(filter));
                } catch (JsonProcessingException e) {
                    throw new HttpMessageNotWritableException("can't write filter: " + e.getMessage(), e);
                }
            }
            final HttpEntity<Void> request = new HttpEntity<Void>(headers);
            return rt.exchange(url, HttpMethod.GET, request, responseType, templateParameters).getBody();
        });
    }

    /**
     * Talk to Linode.
     *
     * @param executor query executor
     * @throws LinodeApiException if an error occurs
     * @throws IllegalArgumentException if {@code executor} is null
     */
    protected void submit(Consumer<RestTemplate> executor) {

        // Sanity check
        if (executor == null)
            throw new IllegalArgumentException("null executor");

        // Send request
        try {
            executor.accept(this.restTemplate);
        } catch (LinodeApiException e) {
            throw e;
        } catch (RestClientException e) {
            if (e.getCause() instanceof SocketException | e.getCause() instanceof NoHttpResponseException)
                this.log.error("error sending Linode API request: " + e);
            else
                this.log.error("error sending Linode API request: " + e, e);
            throw new LinodeApiException("error sending Linode API request", e);
        }
    }

    /**
     * Talk to Linode and get some response.
     *
     * @param executor query executor
     * @param <R> response type
     * @return response received, never null
     * @throws LinodeApiException if an error occurs
     * @throws IllegalArgumentException if {@code executor} is null
     */
    protected <R> R query(Function<RestTemplate, R> executor) {

        // Sanity check
        if (executor == null)
            throw new IllegalArgumentException("null executor");

        // Send request
        R response;
        try {
            response = executor.apply(this.restTemplate);
        } catch (LinodeApiException e) {
            throw e;
        } catch (RestClientException e) {
            if (e.getCause() instanceof SocketException | e.getCause() instanceof NoHttpResponseException)
                this.log.error("error sending Linode API request: " + e);
            else
                this.log.error("error sending Linode API request: " + e, e);
            throw new LinodeApiException("error sending Linode API request", e);
        }

        // Warn if null returned
        if (response == null) {
            this.log.error("rec'd null response from Linode API request");
            throw new LinodeApiException("rec'd null response from Linode API request");
        }

        // Done
        return response;
    }

    /**
     * Build an URL template by combining a base URI with a path template and optional additional query parameters.
     *
     * @param pathTemplate path template
     * @param queryParams additional query parameters in name, value pairs
     * @return complete URL template
     * @throws IllegalArgumentException if either parameter is null
     */
    protected String buildTemplateURL(String pathTemplate, Object... queryParams) {

        // Sanity check
        if (pathTemplate == null)
            throw new IllegalArgumentException("null path");
        if (queryParams == null)
            throw new IllegalArgumentException("null queryParams");

        // Split off query portion
        final String pathPortion;
        String queryPortion;
        final int questionMark = pathTemplate.lastIndexOf('?');
        if (questionMark == -1) {
            pathPortion = pathTemplate;
            queryPortion = null;
        } else {
            pathPortion = pathTemplate.substring(0, questionMark);
            queryPortion = pathTemplate.substring(questionMark + 1);
        }

        // Tack on additional query parameters, if any
        if (queryParams.length > 0) {
            final StringBuilder buf = new StringBuilder();
            if (queryPortion != null)
                buf.append('?').append(queryPortion);
            int i = 0;
            while (i < queryParams.length - 1) {
                if (buf.length() > 0)
                    buf.append('&');
                try {
                    buf.append(URLEncoder.encode(String.valueOf(queryParams[i++]), "UTF-8"));
                    buf.append('=');
                    buf.append(URLEncoder.encode(String.valueOf(queryParams[i++]), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("unexpected exception", e);
                }
            }
            queryPortion = buf.toString();
        }

        // Build URI
        URI pathTemplateURI;
        try {
            pathTemplateURI = new URI(null, null, pathPortion, queryPortion, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("unexpected exception", e);
        }
        final URI escapedURI = this.baseURI.resolve(pathTemplateURI);

        // Un-escape template fields
        final String templateURL = escapedURI.toString().replaceAll("%7B(\\w+)%7D", "{$1}");

        // Done
        return templateURL;
    }

    /**
     * Convert value to JSON.
     *
     * @param value value to convert
     * @return JSON encoding of {@code value}
     * @throws JsonProcessingException if a JSON encoding error occurs
     * @throws IllegalArgumentException if {@code value} is null
     */
    protected String toJson(Object value) throws JsonProcessingException {
        if (value == null)
            throw new IllegalArgumentException("null value");
        final StringWriter buf = new StringWriter();
        try {
            final JsonGenerator generator = this.objectMapper.getFactory().createGenerator(buf);
            objectMapper.writer().writeValue(generator, value);
            generator.flush();
        } catch (IOException e) {
            throw new RuntimeException("unexpected exception", e);
        }
        return buf.toString();
    }
}

