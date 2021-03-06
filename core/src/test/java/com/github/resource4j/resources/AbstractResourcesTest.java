package com.github.resource4j.resources;

import static com.github.resource4j.ResourceKey.key;
import static com.github.resource4j.objects.parsers.ResourceParsers.string;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import com.github.resource4j.OptionalString;
import org.junit.Before;
import org.junit.Test;

import com.github.resource4j.MandatoryString;

import example.impl.ConcreteAction;

public abstract class AbstractResourcesTest {

    protected Resources resources;

    @Before
    public void setup() {
        resources = createResources();
    }

    protected abstract Resources createResources();

    public void shutdown() {
        resources = null;
    }

    @Test
    public void testGetValueFromExistingBundle() {
        MandatoryString value = resources.get(key("test", "value"), Locale.US).notNull();
        assertEquals("success", value.asIs());
    }

    @Test
    public void testGetValueFromDefaultBundleIfBundleNotSpecified() {
        MandatoryString value = resources.get(key("defaultValue"), Locale.US).notNull();
        assertEquals("success", value.asIs());
    }

    @Test
    public void testGetValueFromExistingBundleSpecifiedByClass() {
        MandatoryString value = resources.get(key(ConcreteAction.class, "name"), Locale.US).notNull();
        assertEquals("Concrete Action", value.asIs());
    }
    
    @Test
    public void testValueSourceFromExistingBundleSpecifiedByClass() {
        MandatoryString value = resources.get(key(ConcreteAction.class, "name"), Locale.US).notNull();
        assertThat(value.resolvedSource(), endsWith("ConcreteAction.properties"));
    }
    
    @Test
    public void testValueFormatIndependentFromBundleLocale() {
        int value = resources.get(key(ConcreteAction.class, "number"), Locale.US).notNull().as(Integer.class);
        assertEquals(3000000, value);
    }
   
    @Test
    public void testGetValueFromExistingBundleSpecifiedByClassAndExistingCountryLocale() {
        OptionalString value = resources.get(key(ConcreteAction.class, "name"), Locale.GERMANY);
        assertEquals("Aktion", value.asIs());
    }

    @Test
    public void testValueSourceFromExistingBundleSpecifiedByClassAndExistingCountryLocale() {
        OptionalString value = resources.get(key(ConcreteAction.class, "name"), Locale.GERMANY);
        assertNotNull(value.asIs());
        assertThat(value.resolvedSource(), endsWith("ConcreteAction-de_DE.properties"));
    }
    
    @Test
    public void testGetValueFromExistingBundleSpecifiedByClassAndExistingLanguageLocale() {
        OptionalString value = resources.get(key(ConcreteAction.class, "name"), Locale.GERMAN);
        assertEquals("Konkret Aktion", value.asIs());
    }

    @Test
    public void testGetValueFromDefaultBundleWhenSearchingInExistingBundleSpecifiedByClass() {
        OptionalString value =
                resources.get(key(ConcreteAction.class, "defaultName"), Locale.US);
        assertEquals("ActionName", value.or(resources.get(key("defaultName"), Locale.US).asIs()).asIs());
    }

    @Test
    public void testGetBinaryDataFromExistingBundle() {
        assertEquals("value=success",
                resources.contentOf("test.properties", Locale.US).parsedTo(string("iso-8859-1")).asIs().trim());
    }

    @Test
    public void testGetBinaryDataFromExistingBundleForSpecificLocale() {
        assertEquals("value=Erfolg",
                resources.contentOf("test.properties", Locale.GERMANY)
                	.parsedTo(string("iso-8859-1"))
                	.asIs()
                	.trim());
    }

}
