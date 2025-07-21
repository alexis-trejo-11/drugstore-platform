package microservice.ecommerce_cart_service.app.domain.port.in.usecase;

import microservice.ecommerce_cart_service.app.application.command.afterwards.CreateAfterwardsCommand;
import microservice.ecommerce_cart_service.app.application.command.afterwards.RemoveAfterwardsCommand;
import microservice.ecommerce_cart_service.app.application.dto.AfterwardsSummary;
import microservice.ecommerce_cart_service.app.application.queries.GetCartAfterwardsQuery;

public interface AfterwardUseCase {

    AfterwardsSummary getCartAfterwards(GetCartAfterwardsQuery query);
    void moveItemToAfterwards(CreateAfterwardsCommand command);
    void removeItemFromAfterwards(RemoveAfterwardsCommand command);
}