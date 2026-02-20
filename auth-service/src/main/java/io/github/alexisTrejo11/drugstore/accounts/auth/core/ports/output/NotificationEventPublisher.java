package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.EmailVerificationEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.TwoFactorCodeEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.WelcomeEmailEvent;

public interface NotificationEventPublisher {

  boolean publishEmailVerification(EmailVerificationEvent event);

  boolean publishTwoFactorCode(TwoFactorCodeEvent event);

  boolean publishWelcomeEmail(WelcomeEmailEvent event);
}
