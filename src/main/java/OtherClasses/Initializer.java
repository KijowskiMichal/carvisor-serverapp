package OtherClasses;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Initializer of SpringMVC
 */
@Configuration
@EnableWebMvc
@ComponentScan("Service")
@ComponentScan("RestPackage")
@ComponentScan("HibernatePackage")
@ComponentScan("OtherClasses")
@ComponentScan("Dao")
public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{Initializer.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

}