package user_service.modules.users.core.domain.exceptions;

import libs_kernel.exceptions.BusinessRuleException;

public class WeakPlainPasswordError extends BusinessRuleException {
  public WeakPlainPasswordError(String message) {
    super(message, "WEAK_PLAIN_PASSWORD_ERROR");
  }
}
