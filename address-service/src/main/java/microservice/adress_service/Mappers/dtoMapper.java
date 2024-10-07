package microservice.adress_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import microservice.adress_service.Model.Address;
import microservice.adress_service.Model.ClientAddress;

import java.lang.reflect.Field;

public class dtoMapper {

    public static ClientAddress insertDtoToEntity(ClientAddress address, AddressUpdateDTO addressUpdateDTO) {
        Field[] fields = AddressInsertDTO.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(addressUpdateDTO);
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
