package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Employee.Postion.PositionUpdateDTO;

import java.util.List;

public interface PositionService {
    void createPosition(PositionInsertDTO positionInsertDTO);
    List<PositionDTO> getAllPositions();
    PositionDTO getPositionById(Long positionId);
    boolean updatePosition(PositionUpdateDTO positionUpdateDTO);
    void deletePosition(Long positionId);
    }
