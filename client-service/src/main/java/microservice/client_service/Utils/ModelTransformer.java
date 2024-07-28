package microservice.client_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import microservice.client_service.Model.Address;
import microservice.client_service.Model.Client;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ModelTransformer {

    public static Address insertDtoUpdate(Address address, AddressInsertDTO addressInsertDTO) {
        Field[] fields = AddressInsertDTO.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(addressInsertDTO);
                if (value != null) {
                    Field addressField = Address.class.getDeclaredField(field.getName());
                    addressField.setAccessible(true);
                    addressField.set(address, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.fillInStackTrace();
                throw new RuntimeException("Can't Parse Data");
            }
        }
        return address;
    }
}
