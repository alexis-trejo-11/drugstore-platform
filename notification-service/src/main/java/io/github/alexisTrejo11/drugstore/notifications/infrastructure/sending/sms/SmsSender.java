package io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * SMS sender implementation using Twilio
 * 
 * This component handles sending SMS messages through Twilio's API
 */
@Component
public class SmsSender {

  private static final Logger log = LoggerFactory.getLogger(SmsSender.class);

  @Value("${twilio.account-sid}")
  private String accountSid;

  @Value("${twilio.auth-token}")
  private String authToken;

  @Value("${twilio.phone-number}")
  private String fromPhoneNumber;

  @Value("${twilio.enabled:true}")
  private boolean twilioEnabled;

  /**
   * Initialize Twilio client
   */
  @PostConstruct
  public void init() {
    if (twilioEnabled) {
      try {
        Twilio.init(accountSid, authToken);
        log.info("Twilio client initialized successfully with phone number: {}", fromPhoneNumber);
      } catch (Exception e) {
        log.error("Failed to initialize Twilio client", e);
        throw new RuntimeException("Twilio initialization failed", e);
      }
    } else {
      log.warn("Twilio is disabled in configuration");
    }
  }

  /**
   * Send an SMS message
   * 
   * @param to      Recipient phone number (E.164 format: +1234567890)
   * @param content Message content (max 1600 characters)
   * @return Message SID from Twilio for tracking
   * @throws RuntimeException if sending fails
   */
  public String sendSms(String to, String content) {
    if (!twilioEnabled) {
      log.warn("Twilio is disabled - SMS not sent to: {}", to);
      throw new RuntimeException("Twilio is disabled");
    }

    try {
      log.info("Preparing to send SMS to: {}", to);

      // Validate phone number format
      if (!isValidPhoneNumber(to)) {
        throw new IllegalArgumentException("Invalid phone number format: " + to);
      }

      // Validate message length
      if (content.length() > 1600) {
        log.warn("SMS content exceeds 1600 characters, truncating...");
        content = content.substring(0, 1597) + "...";
      }

      // Send message using Twilio
      Message message = Message.creator(
          new PhoneNumber(to), // To number
          new PhoneNumber(fromPhoneNumber), // From number
          content // Message body
      ).create();

      String messageSid = message.getSid();
      String status = message.getStatus().toString();

      log.info("SMS sent successfully to: {} with SID: {} and status: {}",
          to, messageSid, status);

      return messageSid;

    } catch (IllegalArgumentException e) {
      log.error("Invalid arguments for SMS to: {}", to, e);
      throw e;
    } catch (Exception e) {
      log.error("Failed to send SMS to: {}", to, e);
      throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
    }
  }

  /**
   * Send SMS with status callback (for delivery tracking)
   */
  public String sendSmsWithCallback(String to, String content, String callbackUrl) {
    if (!twilioEnabled) {
      log.warn("Twilio is disabled - SMS not sent to: {}", to);
      throw new RuntimeException("Twilio is disabled");
    }

    try {
      log.info("Sending SMS with callback to: {}", to);

      Message message = Message.creator(
          new PhoneNumber(to),
          new PhoneNumber(fromPhoneNumber),
          content)
          .setStatusCallback(callbackUrl) // Webhook for delivery status
          .create();

      String messageSid = message.getSid();
      log.info("SMS with callback sent to: {} with SID: {}", to, messageSid);

      return messageSid;

    } catch (Exception e) {
      log.error("Failed to send SMS with callback to: {}", to, e);
      throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
    }
  }

  /**
   * Validate phone number format (E.164)
   * 
   * E.164 format: +[country code][subscriber number]
   * Example: +14155552671
   */
  public boolean isValidPhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      return false;
    }

    // E.164 format: starts with +, followed by 1-15 digits
    String e164Regex = "^\\+[1-9]\\d{1,14}$";
    return phoneNumber.matches(e164Regex);
  }

  /**
   * Format phone number to E.164 if needed
   * 
   * This is a simple formatter for common formats
   * For production, consider using libphonenumber library
   */
  public String formatToE164(String phoneNumber, String defaultCountryCode) {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      return null;
    }

    // Remove all non-digit characters
    String cleaned = phoneNumber.replaceAll("[^0-9+]", "");

    // If already in E.164 format
    if (cleaned.startsWith("+")) {
      return cleaned;
    }

    // Add country code if not present
    if (!cleaned.startsWith(defaultCountryCode)) {
      cleaned = defaultCountryCode + cleaned;
    }

    // Add + prefix
    if (!cleaned.startsWith("+")) {
      cleaned = "+" + cleaned;
    }

    return cleaned;
  }

  /**
   * Get Twilio account balance (for monitoring)
   */
  public String getAccountBalance() {
    if (!twilioEnabled) {
      return "Twilio disabled";
    }

    try {
      var account = com.twilio.rest.api.v2010.Account.fetcher(accountSid).fetch();
      return account.getBalance().toString();
    } catch (Exception e) {
      log.error("Failed to fetch Twilio account balance", e);
      return "Error fetching balance";
    }
  }
}
