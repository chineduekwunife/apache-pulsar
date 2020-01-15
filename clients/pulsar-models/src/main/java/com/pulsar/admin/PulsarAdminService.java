package com.pulsar.admin;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;

/**
 * Service used for creating tenants and namespaces on pulsar
 * The tenant is obtained by combining "fusion-" and the kubernetes namespace e.g. fusion-dev or fusion-app.
 * The namespace is the fusion app.
 */
public class PulsarAdminService {

    private static final Logger LOG = LoggerFactory.getLogger(PulsarAdminService.class);

    private static final String TENANT_PREFIX = "fusion-";

    private final PulsarAdmin pulsarAdmin;
    private final String kubeNamespace;

    public PulsarAdminService(PulsarAdmin pulsarAdmin, String kubeNamespace) {
        this.pulsarAdmin = pulsarAdmin;
        this.kubeNamespace = kubeNamespace;
    }

    public List<String> tenants() throws PulsarAdminException {
        return pulsarAdmin.tenants().getTenants();
    }

    public List<String> clusters() throws PulsarAdminException {
        return pulsarAdmin.clusters().getClusters();
    }

    public List<String> namespaces(String tenant) throws PulsarAdminException {
        return pulsarAdmin.namespaces().getNamespaces(tenant);
    }

    /**
     * @param topic     string e.g. TEST_TOPIC
     * @param namespace where topic lives e.g FUSION_APP
     * @return pulsar topic e.g. persistent://{tenant}/{namespace}/{topic}
     */
    public String topic(String topic, String namespace) throws PulsarAdminException {
        String tenant = TENANT_PREFIX + kubeNamespace;

        ensureTenantAndNamespaceExistInCluster(tenant, namespace);

        return format("persistent://%s/%s/%s", tenant, namespace, topic);
    }

    private void ensureTenantAndNamespaceExistInCluster(String tenant, String namespace) throws PulsarAdminException {
        if (!tenants().contains(tenant)) {
            //create tenant in cluster
            pulsarAdmin.tenants().createTenant(tenant, new TenantInfo(Collections.emptySet(), new HashSet<>(clusters())));
            LOG.info("Created tenant [{}] on pulsar cluster", tenant);
        } else {
            LOG.info("Tenant [{}] already exists on pulsar cluster", tenant);
        }

        if (!namespaces(tenant).contains(tenant + "/" + namespace)) {
            //create namespace in cluster
            pulsarAdmin.namespaces().createNamespace(tenant + "/" + namespace);
            LOG.info("Created namespace [{}] on pulsar cluster", namespace);
        } else {
            LOG.info("Namespace [{}] already exists on pulsar cluster", namespace);
        }
    }

}
