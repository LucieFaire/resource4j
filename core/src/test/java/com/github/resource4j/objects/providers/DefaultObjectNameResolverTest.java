package com.github.resource4j.objects.providers;

import com.github.resource4j.objects.providers.resolvers.DefaultObjectNameResolver;
import com.github.resource4j.resources.context.ResourceResolutionContext;
import org.junit.Test;

import java.util.Locale;

import static com.github.resource4j.resources.context.ResourceResolutionContext.withoutContext;
import static org.junit.Assert.*;

public class DefaultObjectNameResolverTest {

    public final static String EXAMPLE_FILE = "/folder/file.name";

    @Test(expected =  NullPointerException.class)
    public void testNullFileNameResolutionWithoutContext() {
        DefaultObjectNameResolver resolver = new DefaultObjectNameResolver();
        resolver.resolve(null, withoutContext());
    }


    @Test(expected =  IllegalArgumentException.class)
    public void testEmptyFileNameResolutionWithoutContext() {
        DefaultObjectNameResolver resolver = new DefaultObjectNameResolver();
        resolver.resolve("", withoutContext());
    }

    @Test
    public void testResolutionWithEmptyContext() {
        DefaultObjectNameResolver resolver = new DefaultObjectNameResolver();
        String result = resolver.resolve(EXAMPLE_FILE, withoutContext());
        assertEquals(EXAMPLE_FILE, result);
    }

    @Test
    public void testResolutionWithLocaleContext() {
        DefaultObjectNameResolver resolver = new DefaultObjectNameResolver();
        String result = resolver.resolve(EXAMPLE_FILE, ResourceResolutionContext.in(Locale.US));
        assertEquals("/folder/file-en_US.name", result);
    }

    @Test
    public void testResolutionWithLocaleWebContext() {
        DefaultObjectNameResolver resolver = new DefaultObjectNameResolver();
        String result = resolver.resolve(EXAMPLE_FILE, ResourceResolutionContext.in(Locale.US, "WEB"));
        assertEquals("/folder/file-en_US-WEB.name", result);
    }

}
