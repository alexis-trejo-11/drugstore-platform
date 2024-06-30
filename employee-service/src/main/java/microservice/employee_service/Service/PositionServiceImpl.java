package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionReturnDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionUpdateDTO;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Model.enums.ClassificationWorkday;
import microservice.employee_service.Repository.PositionRepository;
import microservice.employee_service.Utils.ModelTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl {

    private final PositionRepository positionRepository;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Async
    @Transactional
    public void createPosition(PositionInsertDTO positionInsertDTO) {
        try {
            Position position = ModelTransform.insertDtoToPosition(positionInsertDTO);

            positionRepository.saveAndFlush(position);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Async
    public List<PositionReturnDTO> getAllPositions() {
        List<Position> positions = positionRepository.findAll();

        return positions
                .stream()
                .map(ModelTransform::positionToReturnDTO)
                .collect(Collectors.toList());
    }

    @Async
    public Result<PositionReturnDTO> getPositionById(Long positionId) {
        try {
            Optional<Position> position = positionRepository.findById(positionId);
            if (position.isPresent()) {
                PositionReturnDTO positionReturnDTO = ModelTransform.positionToReturnDTO(position.get());
                return Result.success(positionReturnDTO);
            } else {
                return Result.error("Position With Id: " + positionId + " Not Found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }
    @Async
    @Transactional
    public Result<PositionReturnDTO> updatePosition (PositionUpdateDTO positionUpdateDTO) {
        try {
            Optional<Position> positionOptional = positionRepository.findById(positionUpdateDTO.getId());
            if (positionOptional.isPresent()) {
                Position position = positionOptional.get();
                position.setPositionName(positionUpdateDTO.getPositionName());
                position.setSalary(positionUpdateDTO.getSalary());
                position.setClassificationWorkday(ClassificationWorkday.valueOf(positionUpdateDTO.getClassificationWorkday()));
                position.setUpdatedAt(LocalDateTime.now());

                positionRepository.saveAndFlush(position);

                PositionReturnDTO positionReturnDTO =ModelTransform.positionToReturnDTO(position);
                return Result.success(positionReturnDTO);
            } else {
                return Result.error("Position With Id: " + positionUpdateDTO.getPositionId() + " Not Found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Async
    @Transactional
    public Result<String> deletePosition (Long positionId){
        try {
            Optional<Position> positionOptional = positionRepository.findById(positionId);
            if (positionOptional.isPresent()) {
                positionRepository.deleteById(positionId);
                return Result.success("Position deleted successfully");
            } else {
                return Result.error("Position With Id: " + positionId + " Not Found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }
}


