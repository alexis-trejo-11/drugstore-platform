package microservice.employee_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionUpdateDTO;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Model.enums.ClassificationWorkday;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Position insertDtoToEntity(PositionInsertDTO positionInsertDTO);

    @Mapping(target = "classificationWorkday", expression = "java(classificationWorkdayToString(position.getClassificationWorkday()))")
    PositionDTO entityToDTO(Position position);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "classificationWorkday", expression = "java(stringToClassificationWorkday(positionUpdateDTO.getClassificationWorkday()))")
    void updateDTOtoEntity(PositionUpdateDTO positionUpdateDTO, @MappingTarget Position position);

    default ClassificationWorkday stringToClassificationWorkday(String classificationWorkday) {
        return classificationWorkday != null ? ClassificationWorkday.valueOf(classificationWorkday) : null;
    }

    default String classificationWorkdayToString(ClassificationWorkday classificationWorkday) {
        return classificationWorkday != null ? classificationWorkday.name() : null;
        }
}
