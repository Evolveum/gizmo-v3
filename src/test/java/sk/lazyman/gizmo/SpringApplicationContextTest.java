/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.lazyman.gizmo;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author lazyman
 */
@ContextConfiguration(locations = {
        "classpath:ctx-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SpringApplicationContextTest extends BaseTest {

    @Test
    public void initApplicationContext() throws Exception {
        assertNotNull(sessionFactoryBean);

        createSQLSchema("./target/postgresql-schema.sql");
    }

    private void createSQLSchema(String fileName) throws Exception {
        org.hibernate.cfg.Configuration configuration = new Configuration();
        Properties properties = new Properties();
        properties.putAll(sessionFactoryBean.getJpaPropertyMap());
        configuration.setProperties(properties);

//        AbstractQuerydslProcessor

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(properties).build();
        MetadataSources sources = new MetadataSources(registry);
        List<Class> classes = listClasses("sk.lazyman.gizmo.data");
        classes.forEach(clazz -> sources.addAnnotatedClass(clazz));



        Metadata metadata = sources.getMetadataBuilder().build();

        System.out.println("Dialect: " + properties.getProperty("hibernate.dialect"));
        System.out.println("naming strategy: " + properties.getProperty("hibernate.ejb.naming_strategy"));

        addAnnotatedClasses("sk.lazyman.gizmo.data", configuration);

//        SchemaExport export = new SchemaExport();
//        export.setOutputFile(fileName);
//        export.setDelimiter(";");
//        export.create(EnumSet.of(TargetType.SCRIPT, TargetType.STDOUT), metadata);

    }

    private void addAnnotatedClasses(String packageName, Configuration configuration) throws Exception {
        List<Class> classes = listClasses(packageName);
        for (Class clazz : classes) {
            configuration.addAnnotatedClass(clazz);
        }
    }

    private List<Class> listClasses(String basePackage) throws IOException, ClassNotFoundException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class> candidates = new ArrayList<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
            }
        }
        return candidates;
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }
}
