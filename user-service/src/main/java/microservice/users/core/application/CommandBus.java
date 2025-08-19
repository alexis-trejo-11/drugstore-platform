package microservice.users.core.application;

import microservice.users.core.application.command.Command;

public interface CommandBus {
    void dispatch(Command command);
}
