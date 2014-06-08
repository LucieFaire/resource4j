package com.github.resource4j.resources;

import static com.github.resource4j.ResourceKey.bundle;
import static com.github.resource4j.resources.resolution.ResourceResolutionContext.in;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.resource4j.OptionalString;
import com.github.resource4j.ResourceKey;
import com.github.resource4j.files.MissingResourceFileException;
import com.github.resource4j.files.ResourceFile;
import com.github.resource4j.generic.GenericOptionalString;
import com.github.resource4j.resources.resolution.ResourceResolutionContext;

/**
 *
 * @author Ivan Gammel
 */
public class InMemoryResources extends AbstractResources implements EditableResources {

    private final String instanceId = "memory:" + hashCode();
	
    private Map<ResourceResolutionContext, Map<ResourceKey,String>> storage = new HashMap<>();
    
    @Override
    public void put(ResourceKey key, Locale locale, Object value) {
    	put(key, in(locale), value);
    }

    @Override
    public void put(ResourceKey key, ResourceResolutionContext context, Object value) {
        Map<ResourceKey, String> localeResources = storage.get(context);
        if (localeResources == null) {
            localeResources = new HashMap<>();
            storage.put(context, localeResources);
        }
    }

    @Override
    public void remove(ResourceKey key, Locale locale) {
    	remove(key, in(locale));
    }
    
    @Override
    public void remove(ResourceKey key, ResourceResolutionContext context) {
        Map<ResourceKey, String> localeResources = storage.get(context);
        if (localeResources == null) {
            return;
        }
        localeResources.remove(key);
    }
    @Override
	public OptionalString get(ResourceKey key, ResourceResolutionContext context) {
    	String value = null;
        Map<ResourceKey, String> contextResources = storage.get(context);
        if (contextResources != null) {
        	value = contextResources.get(key);
        }
		return new GenericOptionalString(instanceId, key, value);
    }

    @Override
    public ResourceFile contentOf(String name, ResourceResolutionContext context) {
        throw new MissingResourceFileException(bundle(name));
    }


}