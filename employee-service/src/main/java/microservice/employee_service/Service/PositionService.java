package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PositionService {
    void createPosition(PositionInsertDTO positionInsertDTO);
    Page<PositionDTO> getPositionsSortedByNameAsc(Pageable pageable, boolean sortedAsc);
    PositionDTO getPositionById(Long positionId);
    Result<Void> updatePosition(PositionUpdateDTO positionUpdateDTO);
    Result<Void> deletePosition(Long positionId);
    }
