package com.github.resource4j.resources;

import static com.github.resource4j.ResourceKey.bundle;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.github.resource4j.*;
import com.github.resource4j.generic.objects.factory.ClasspathResourceObjectFactory;
import com.github.resource4j.generic.objects.factory.ResourceObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resource4j.files.lookup.*;
import com.github.resource4j.generic.values.GenericOptionalString;
import com.github.resource4j.resources.resolution.ResourceResolutionContext;

/**
 * @author Ivan Gammel
 */
public class CustomizableResources extends AbstractResources {

    public static final ResourceKey DEFAULT_APPLICATION_RESOURCES = bundle("i18n.resources");

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected ResourceKey defaultResourceBundle;

    private ResourceFileEnumerationStrategy fileEnumerationStrategy;

    private ResourceObjectFactory fileFactory;

    private ResourceBundleParser bundleParser;

    public CustomizableResources() {
        this.defaultResourceBundle = DEFAULT_APPLICATION_RESOURCES;
        LOG.debug("Configured default resource bundle: {}", defaultResourceBundle);
    }

    public CustomizableResources(String defaultBundle) {
        this.defaultResourceBundle = ResourceKey.bundle(defaultBundle);
        LOG.debug("Configured default resource bundle: {}", defaultBundle);
    }
    
    @PostConstruct
    public void verifyConfiguration() {
    	getFileEnumerationStrategy();
    	getFileFactory();
    	getBundleParser();
    }

    public void setDefaultResourceBundle(String defaultBundle) {
        this.defaultResourceBundle = ResourceKey.bundle(defaultBundle);
        LOG.debug("Configured default resource bundle: {}", defaultBundle);
    }

    public void setFileEnumerationStrategy(ResourceFileEnumerationStrategy fileEnumerationStrategy) {
        this.fileEnumerationStrategy = fileEnumerationStrategy;
        LOG.debug("Configured file enumeration strategy: {}", fileEnumerationStrategy.getClass().getSimpleName());
    }
    
    public ResourceFileEnumerationStrategy getFileEnumerationStrategy() {
    	if (fileEnumerationStrategy == null) {
    		setFileEnumerationStrategy(new BasicResourceFileEnumerationStrategy());
    	}
    	return fileEnumerationStrategy;
    }

    public void setFileFactory(ResourceObjectFactory fileFactory) {
        this.fileFactory = fileFactory;
        LOG.debug("Configured file factory: {}", fileFactory.getClass().getSimpleName());
    }
    
    public ResourceObjectFactory getFileFactory() {
    	if (fileFactory == null) {
    		setFileFactory(new ClasspathResourceObjectFactory());
    	}
    	return fileFactory;
    }

    public void setBundleParser(ResourceBundleParser bundleParser) {
        this.bundleParser = bundleParser;
        LOG.debug("Configured resource bundle parser: {}", bundleParser.getClass().getSimpleName());
    }

    public ResourceBundleParser getBundleParser() {
    	if (bundleParser == null) {
    		setBundleParser(new PropertyResourceBundleParser());
    	}
    	return bundleParser;
    }


	@Override
	public OptionalString get(ResourceKey key, ResourceResolutionContext context) {
        String bundleName = getBundleParser().getResourceFileName(key);
        String defaultBundleName = getBundleParser().getResourceFileName(defaultResourceBundle);
        String[] bundleOptions = bundleName != null
                ? new String[] { bundleName, defaultBundleName }
                : new String[] { defaultBundleName };
        String fullKey = key.getBundle() + '#' + key.getId();
        String shortKey = key.getId();
        List<String> options = getFileEnumerationStrategy().enumerateFileNameOptions(bundleOptions, context);
        
        Throwable suppressedException = null;
        String value = null;
        String resolvedSource = null;
        
        for (String option : options) {
            try {
                ResourceObject object = getFileFactory().getObject(key.getBundle(), option);
                Map<String, String> properties = getBundleParser().parse(object);
                if (properties.containsKey(shortKey)) {
                    value = properties.get(shortKey);
                    resolvedSource = object.resolvedName();
					break;
                } else if (properties.containsKey(fullKey)) {
                	value = properties.get(fullKey);
                    resolvedSource = object.resolvedName();
                	break;
                }
            } catch (MissingResourceObjectException e) {
                suppressedException = e;
            }
        }
        return new GenericOptionalString(resolvedSource, key, value, suppressedException);
    }

    @Override
    public ResourceObject contentOf(String name, ResourceResolutionContext context) {
        List<String> options = getFileEnumerationStrategy().enumerateFileNameOptions(new String[] { name }, context);
        ResourceObject object = null;
        for (String option : options) {
            try {
                object = getFileFactory().getObject(name, option);
                break;
            } catch (MissingResourceObjectException e) {
                LOG.trace("Object not found: {}", option);
            }
        }
        if (object != null) {
            return object;
        }
        throw new MissingResourceObjectException(name);
    }

}
