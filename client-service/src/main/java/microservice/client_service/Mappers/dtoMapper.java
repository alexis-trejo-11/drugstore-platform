package microservice.client_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import microservice.client_service.Model.Address;

import java.lang.reflect.Field;

public class dtoMapper {

    public static Address insertDtoToEntity(Address address, AddressInsertDTO addressInsertDTO) {
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
