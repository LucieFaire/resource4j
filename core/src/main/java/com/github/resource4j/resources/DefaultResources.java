package com.github.resource4j.resources;

import static com.github.resource4j.ResourceKey.bundle;
import static com.github.resource4j.resources.cache.CachedValue.cached;
import static com.github.resource4j.resources.cache.CachedValue.missingValue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.github.resource4j.OptionalString;
import com.github.resource4j.ResourceKey;
import com.github.resource4j.MissingResourceObjectException;
import com.github.resource4j.ResourceObject;
import com.github.resource4j.generic.values.GenericOptionalString;
import com.github.resource4j.resources.cache.CachedValue;
import com.github.resource4j.resources.cache.ResourceCache;
import com.github.resource4j.resources.cache.SimpleResourceCache;
import com.github.resource4j.resources.resolution.ResourceResolutionContext;

/**
 * @author Ivan Gammel
 * @since 1.0
 */
public class DefaultResources extends CustomizableResources {

    /**
     *
     */
    public final static ResourceKey DEFAULT_APPLICATION_RESOURCES = bundle("i18n.resources");

    private ResourceCache<OptionalString> valueCache = new SimpleResourceCache<>();

    private ResourceCache<ResourceObject> fileCache = new SimpleResourceCache<>();

    public DefaultResources() {
        this.defaultResourceBundle = DEFAULT_APPLICATION_RESOURCES;
    }

    public DefaultResources(String defaultBundle) {
        this.defaultResourceBundle = ResourceKey.bundle(defaultBundle);
    }
    
    @Override
	public OptionalString get(ResourceKey key, ResourceResolutionContext context) {
    	OptionalString result = lookup(key, context);
    	if (result == null) {
    		result = new GenericOptionalString(null, key, null); 
    	}
    	return result;
    }

    protected OptionalString lookup(ResourceKey key, ResourceResolutionContext context) {
        // first, try to get the value from cache
        CachedValue<OptionalString> cachedValue = valueCache.get(key, context);
        if (cachedValue != null) {
        	return cachedValue.get();
        }
        
        // synchronizing on write
        synchronized (valueCache) {
        	// second check after synchronization
            cachedValue = valueCache.get(key, context);
            if (cachedValue != null) {
            	return cachedValue.get();
            }
        	
        	// value is still not in cache, load related bundles to cache
            tryLoadToCache(key.bundle(), context);
            
	        // finally, try again
	        cachedValue = valueCache.get(key, context);
	        if (cachedValue != null) {
	            return cachedValue.get();
	        }
	        // value not found: record this search result to cache
	        valueCache.put(key, context, missingValue(OptionalString.class));
        }
        return null;
    }
	protected void tryLoadToCache(ResourceKey bundle, 
			ResourceResolutionContext context) throws MissingResourceObjectException {
		String fileName = getBundleParser().getResourceFileName(bundle);
		String defaultFileName = getBundleParser().getResourceFileName(defaultResourceBundle);
		String[] bundleOptions = fileName != null
		        ? new String[] { fileName, defaultFileName }
		        : new String[] { defaultFileName };
		List<String> options = getFileEnumerationStrategy().enumerateFileNameOptions(bundleOptions, context);
		boolean found = false;
		for (String option : options) {
		    try {
		        ResourceObject file = getFileFactory().getObject(bundle.getBundle(), option);
		        Map<String, String> properties = getBundleParser().parse(file);
		        for (Map.Entry<String, String> property : properties.entrySet()) {
		            String id = property.getKey();
		            ResourceKey propertyKey = bundle.child(id);
		            String propertyValue = property.getValue();
		            OptionalString string = new GenericOptionalString(file.resolvedName(), bundle, propertyValue);
		            valueCache.putIfAbsent(propertyKey, context, cached(string, file.resolvedName()));
		        }
		        found = true;
		    } catch (MissingResourceObjectException e) {
		    	// ignore, since we are processing multiple options
		    }
		}
		if (!found) {
			throw new MissingResourceObjectException(bundle.getBundle());
		}
	}

    @Override
    public ResourceObject contentOf(String name, ResourceResolutionContext context) {
        ResourceKey key = bundle(name);
        // first, try to find the file in cache
        CachedValue<ResourceObject> cachedFile = fileCache.get(key, context);
        if (cachedFile != null) {
            if (cachedFile.isMissing()) {
                throw new MissingResourceObjectException(name);
            } else {
                return cachedFile.get();
            }
        }
        // not cached, try to find it
        List<String> options = getFileEnumerationStrategy().enumerateFileNameOptions(new String[] { name }, context);
        for (String option : options) {
            try {
                ResourceObject file = getFileFactory().getObject(name, option);
                file.asStream().close();
                fileCache.put(key, context, cached(file, file.resolvedName()));
                return file;
            } catch (MissingResourceObjectException | IOException e) {
            }
        }
        // file not found: record this search result in cache
        fileCache.put(key, context, missingValue(ResourceObject.class));
        throw new MissingResourceObjectException(name);
    }
}
