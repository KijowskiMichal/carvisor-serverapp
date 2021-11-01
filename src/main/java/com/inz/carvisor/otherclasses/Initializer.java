package com.inz.carvisor.otherclasses;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Initializer of SpringMVC
 */
@Configuration
@EnableWebMvc
@ComponentScan("com/inz/carvisor/controller")
@ComponentScan("com/inz/carvisor/hibernatepackage")
@ComponentScan("com/inz/carvisor/otherclasses")
@ComponentScan("com/inz/carvisor/util")
@ComponentScan("com/inz/carvisor/dao")
@ComponentScan("com/inz/carvisor/service")
@ComponentScan("com/inz/carvisor/constants")
@ComponentScan("com/inz/carvisor/entities")
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