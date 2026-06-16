package io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Dedicated Thymeleaf engine for plain-text SMS templates.
 */
@Configuration
public class ThymeleafTemplateConfig {

  @Bean
  public TemplateEngine smsTemplateEngine() {
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/notifications/sms/");
    resolver.setSuffix(".txt");
    resolver.setTemplateMode(TemplateMode.TEXT);
    resolver.setCharacterEncoding("UTF-8");
    resolver.setCacheable(true);

    TemplateEngine engine = new TemplateEngine();
    engine.setTemplateResolver(resolver);
    return engine;
  }
}
