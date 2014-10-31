package sk.lazyman.gizmo;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
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
import sk.lazyman.gizmo.util.GizmoNamingStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author lazyman
 */
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/ctx-web.xml",
        "../../../ctx-test.xml"})
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
        configuration.setNamingStrategy(new GizmoNamingStrategy());

        System.out.println("Dialect: " + properties.getProperty("hibernate.dialect"));

        addAnnotatedClasses("sk.lazyman.gizmo.data", configuration);

        SchemaExport export = new SchemaExport(configuration);
        export.setOutputFile(fileName);
        export.setDelimiter(";");
        export.execute(true, false, false, true);
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
