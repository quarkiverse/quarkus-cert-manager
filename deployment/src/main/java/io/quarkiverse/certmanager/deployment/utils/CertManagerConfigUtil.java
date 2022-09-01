package io.quarkiverse.certmanager.deployment.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import io.dekorate.utils.Maps;
import io.quarkiverse.certmanager.deployment.CertificateConfig;
import io.quarkiverse.certmanager.deployment.SubjectConfig;

public final class CertManagerConfigUtil {
    private static final String QUARKUS_PREFIX = "quarkus.certificate.";
    private static final String DEKORATE_PREFIX = "dekorate.certificate.";
    private static final String MULTIPART_SEPARATOR_PATTERN = Pattern.quote(".");

    private CertManagerConfigUtil() {

    }

    public static Map<String, Object> transformToDekorateProperties(CertificateConfig certificateConfig) {
        Config config = ConfigProvider.getConfig();
        Map<String, Object> certificateProperties = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(k -> k.startsWith(QUARKUS_PREFIX))
                .collect(Collectors.toMap(k -> k.replaceFirst(QUARKUS_PREFIX, ""), k -> config.getValue(k, String.class)));

        certificateProperties = expandProperties(certificateProperties);
        // workaround to deal with properties that are Optional<List<String>>. ConfigProvider.getConfig() retrieves these
        // properties as string "a,b" instead of the type Optional<List<String>>, so we need to manually add it.
        overwriteNonStringProperties(certificateConfig, certificateProperties);
        return certificateProperties;
    }

    private static void overwriteNonStringProperties(CertificateConfig config, Map<String, Object> props) {
        config.dnsNames().ifPresent(l -> put(props, "dns-names", l));
        config.ipAddresses().ifPresent(l -> put(props, "ip-addresses", l));
        config.uris().ifPresent(l -> put(props, "uris", l));
        config.emailAddresses().ifPresent(l -> put(props, "email-addresses", l));
        config.usages().ifPresent(l -> put(props, "usages", l));
        if (config.ca().isPresent()) {
            Map<String, Object> caProps = (Map<String, Object>) props.get("ca");
            config.ca().get().crlDistributionPoints().ifPresent(l -> put(caProps, "crl-distribution-points", l));
        }
        if (config.selfSigned().isPresent()) {
            Map<String, Object> ssProps = (Map<String, Object>) props.get("self-signed");
            config.selfSigned().get().crlDistributionPoints().ifPresent(l -> put(ssProps, "crl-distribution-points", l));
        }
        if (config.subject().isPresent()) {
            Map<String, Object> subjectProps = (Map<String, Object>) props.get("subject");
            SubjectConfig subjectConfig = config.subject().get();
            subjectConfig.organizations().ifPresent(l -> put(subjectProps, "organizations", l));
            subjectConfig.countries().ifPresent(l -> put(subjectProps, "countries", l));
            subjectConfig.organizationalUnits().ifPresent(l -> put(subjectProps, "organizational-units", l));
            subjectConfig.localities().ifPresent(l -> put(subjectProps, "localities", l));
            subjectConfig.provinces().ifPresent(l -> put(subjectProps, "provinces", l));
            subjectConfig.streetAddresses().ifPresent(l -> put(subjectProps, "street-addresses", l));
            subjectConfig.postalCodes().ifPresent(l -> put(subjectProps, "postal-codes", l));
        }
        if (config.privateKey().isPresent()) {
            Map<String, Object> privateKeyProps = (Map<String, Object>) props.get("private-key");
            privateKeyProps.put("size", config.privateKey().get().size());
        }
    }

    private static Map<String, Object> expandProperties(Map<String, Object> properties) {
        Map<String, Object> newProperties = new HashMap<>();
        for (String propertyName : properties.keySet()) {
            Object value = properties.get(propertyName);
            String[] parts = propertyName.split(MULTIPART_SEPARATOR_PATTERN);
            if (parts.length == 1) {
                newProperties.put(propertyName, value);
            } else {
                Map<String, Object> nestedProperties = asMap(parts, value);
                Maps.merge(newProperties, nestedProperties);
            }
        }

        return newProperties;
    }

    private static void put(Map<String, Object> map, String key, List<String> value) {
        map.put(key, value.toArray(new String[0]));
    }

    /**
     * Convert a multipart-key value pair to a Map.
     */
    private static Map<String, Object> asMap(String[] keys, Object value) {
        if (keys == null || keys.length == 0) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        if (keys.length == 1) {
            result.put(keys[0], value);
            return result;
        }

        String key = keys[0];
        String[] remaining = new String[keys.length - 1];
        System.arraycopy(keys, 1, remaining, 0, remaining.length);
        Map<String, Object> nested = asMap(remaining, value);
        result.put(key, nested);
        return result;
    }

}
