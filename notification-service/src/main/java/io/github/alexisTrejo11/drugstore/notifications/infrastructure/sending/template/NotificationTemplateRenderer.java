package io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.template;

import java.time.Year;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.TemplateNotFoundException;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;

/**
 * Renders notification templates from classpath files via Thymeleaf.
 *
 * <p>Email templates: {@code templates/notifications/email/{templateId}.html}
 * <p>SMS templates: {@code templates/notifications/sms/{templateId}.txt}
 */
@Component
public class NotificationTemplateRenderer {

  private static final Logger log = LoggerFactory.getLogger(NotificationTemplateRenderer.class);
  private static final String EMAIL_TEMPLATE_PREFIX = "templates/notifications/email/";
  private static final String SMS_TEMPLATE_PREFIX = "templates/notifications/sms/";

  private final TemplateEngine emailTemplateEngine;
  private final TemplateEngine smsTemplateEngine;

  public NotificationTemplateRenderer(
      @Qualifier("templateEngine") TemplateEngine emailTemplateEngine,
      @Qualifier("smsTemplateEngine") TemplateEngine smsTemplateEngine) {
    this.emailTemplateEngine = emailTemplateEngine;
    this.smsTemplateEngine = smsTemplateEngine;
  }

  public String renderEmail(String templateId, Map<String, String> variables, String language) {
    if (templateId == null || templateId.isBlank()) {
      throw new TemplateNotFoundException("(empty)", NotificationChannel.EMAIL.getValue());
    }

    if (!emailTemplateExists(templateId)) {
      throw new TemplateNotFoundException(templateId, NotificationChannel.EMAIL.getValue());
    }

    Context context = buildContext(variables, language);
    String logicalName = "notifications/email/" + templateId;
    log.debug("Rendering email template: {}", logicalName);
    return emailTemplateEngine.process(logicalName, context);
  }

  public String renderSms(String templateId, Map<String, String> variables, String language) {
    if (templateId == null || templateId.isBlank()) {
      throw new TemplateNotFoundException("(empty)", NotificationChannel.SMS.getValue());
    }

    if (!smsTemplateExists(templateId)) {
      throw new TemplateNotFoundException(templateId, NotificationChannel.SMS.getValue());
    }

    Context context = buildContext(variables, language);
    log.debug("Rendering SMS template: {}", templateId);
    return smsTemplateEngine.process(templateId, context);
  }

  public boolean emailTemplateExists(String templateId) {
    return new ClassPathResource(EMAIL_TEMPLATE_PREFIX + templateId + ".html").exists();
  }

  public boolean smsTemplateExists(String templateId) {
    return new ClassPathResource(SMS_TEMPLATE_PREFIX + templateId + ".txt").exists();
  }

  private Context buildContext(Map<String, String> variables, String language) {
    Context context = new Context(resolveLocale(language));
    Map<String, Object> model = new HashMap<>();
    if (variables != null) {
      model.putAll(variables);
    }
    model.putIfAbsent("currentYear", String.valueOf(Year.now().getValue()));
    context.setVariables(model);
    return context;
  }

  private Locale resolveLocale(String language) {
    if (language == null || language.isBlank()) {
      return Locale.ENGLISH;
    }
    return Locale.forLanguageTag(language.replace('_', '-'));
  }
}
