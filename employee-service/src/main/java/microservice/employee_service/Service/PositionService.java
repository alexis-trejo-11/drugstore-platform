package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PositionService {
    void createPosition(PositionInsertDTO positionInsertDTO);
    Page<PositionDTO> getPositionsSortedByNameAsc(Pageable pageable, boolean sortedAsc);
    PositionDTO getPositionById(Long positionId);
    void updatePosition(PositionUpdateDTO positionUpdateDTO);
    void deletePosition(Long positionId);
    boolean validateExisitingPosition(Long positionId);
    }
