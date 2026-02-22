package io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailSender.class);
  private final JavaMailSender mailSender;

  @Autowired
  public EmailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${notification.email.from-name:Drugstore Notifications}")
  private String fromName;

  @Value("${notification.email.reply-to:noreply@drugstore.com}")
  private String replyTo;

  public String sendEmail(
      String to,
      String recipientName,
      String subject,
      String content,
      boolean isHtml) throws MessagingException {

    log.info("Preparing to send email to: {} with subject: {}", to, subject);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      // Set sender
      helper.setFrom(fromEmail, fromName);

      // Set recipient
      if (recipientName != null && !recipientName.trim().isEmpty()) {
        helper.setTo(to);
        // Optionally set personal name: helper.setTo(to, recipientName);
      } else {
        helper.setTo(to);
      }

      // Set reply-to
      helper.setReplyTo(replyTo);

      // Set subject
      helper.setSubject(subject);

      // Set content
      helper.setText(content, isHtml);

      // Set priority (optional)
      message.setHeader("X-Priority", "3"); // 1 = High, 3 = Normal, 5 = Low

      // Send email
      mailSender.send(message);

      // Get message ID for tracking
      String messageId = message.getMessageID();

      log.info("Email sent successfully to: {} with message ID: {}", to, messageId);

      return messageId;

    } catch (MessagingException e) {
      log.error("Failed to send email to: {} with subject: {}", to, subject, e);
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error sending email to: {}", to, e);
      throw new MessagingException("Failed to send email", e);
    }
  }
}
