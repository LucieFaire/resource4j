package com.github.resource4j.thymeleaf;

import com.github.resource4j.objects.providers.ResourceObjectProviders;
import com.github.resource4j.resources.RefreshableResourcesConfigurator;
import com.github.resource4j.spring.config.Resource4jAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.github.resource4j.objects.parsers.ResourceParsers.propertyMap;
import static com.github.resource4j.objects.providers.ResourceObjectProviders.bind;
import static com.github.resource4j.objects.providers.ResourceObjectProviders.classpath;
import static com.github.resource4j.objects.providers.ResourceObjectProviders.patternMatching;
import static com.github.resource4j.objects.providers.resolvers.ResourceObjectProviderPredicates.name;
import static com.github.resource4j.resources.BundleFormat.format;
import static com.github.resource4j.resources.ResourcesConfigurationBuilder.configure;
import static com.github.resource4j.resources.processors.BasicValuePostProcessor.macroSubstitution;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ ComplexInclusionTest.class, Resource4jAutoConfiguration.class, ThymeleafResourceConfiguration.class })
@Configuration
public class ComplexInclusionTest {

    @Bean
    public static RefreshableResourcesConfigurator configurator() {
        return configure()
                .sources(bind(classpath())
                            .to("complex/pages")
                            .objectsLike(name(".\\.html$")),
                         bind(classpath())
                            .to("complex/resources")
                            .objectsLike(name(".\\.properties$")))
                .formats(format(propertyMap(), ".properties"))
                .postProcessingBy(macroSubstitution())
                .get();
    }

    @Autowired
    private TemplateEngine engine;

    @Test
    public void testRenderWithInclusions() {
        Context context = new Context(new Locale("en", "US"));
        Map<String, Object> map = new HashMap<>();
        map.put("count", 1);
        context.setVariable("model", map);
        String content = engine.process("index", context);
    }

}
