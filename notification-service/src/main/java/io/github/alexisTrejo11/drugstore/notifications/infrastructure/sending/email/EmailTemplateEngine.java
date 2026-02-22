package io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.email;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Template engine for processing email templates
 * 
 * Supports simple variable substitution with {{variableName}} syntax
 * and basic conditional rendering
 */
@Component
public class EmailTemplateEngine {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailTemplateEngine.class);
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

  /**
   * Process template by replacing variables with actual values
   * 
   * @param template  Template string with {{variableName}} placeholders
   * @param variables Map of variable names to values
   * @return Processed template string
   */
  public String processTemplate(String template, Map<String, String> variables) {
    if (template == null || template.isEmpty()) {
      log.warn("Empty template provided");
      return "";
    }

    if (variables == null || variables.isEmpty()) {
      log.warn("No variables provided for template processing");
      return template;
    }

    String processed = template;
    Matcher matcher = VARIABLE_PATTERN.matcher(template);

    while (matcher.find()) {
      String variableName = matcher.group(1);
      String value = variables.get(variableName);

      if (value != null) {
        processed = processed.replace("{{" + variableName + "}}", value);
        processed = processed.replace("{{ " + variableName + " }}", value);
      } else {
        log.warn("Variable '{}' not found in provided variables map", variableName);
        // Keep the placeholder if variable not found
      }
    }

    return processed;
  }

  /**
   * Build HTML email wrapper with common structure
   */
  public String wrapInHtmlTemplate(String content, String title) {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>%s</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .header {
                    background-color: #4CAF50;
                    color: white;
                    padding: 20px;
                    text-align: center;
                    border-radius: 5px 5px 0 0;
                }
                .content {
                    background-color: #f9f9f9;
                    padding: 30px;
                    border-radius: 0 0 5px 5px;
                }
                .button {
                    display: inline-block;
                    padding: 12px 24px;
                    background-color: #4CAF50;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                    margin: 20px 0;
                }
                .footer {
                    margin-top: 20px;
                    padding-top: 20px;
                    border-top: 1px solid #ddd;
                    font-size: 12px;
                    color: #666;
                    text-align: center;
                }
                .code {
                    font-size: 24px;
                    font-weight: bold;
                    letter-spacing: 4px;
                    color: #4CAF50;
                    background-color: #f0f0f0;
                    padding: 15px;
                    border-radius: 5px;
                    text-align: center;
                    margin: 20px 0;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>%s</h1>
            </div>
            <div class="content">
                %s
            </div>
            <div class="footer">
                <p>© 2024 Drugstore. All rights reserved.</p>
                <p>This is an automated message, please do not reply.</p>
            </div>
        </body>
        </html>
        """.formatted(title, title, content);
  }

  /**
   * Escape HTML special characters to prevent XSS
   */
  public String escapeHtml(String input) {
    if (input == null) {
      return null;
    }
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}