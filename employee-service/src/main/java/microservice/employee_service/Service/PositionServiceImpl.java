package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionUpdateDTO;
import microservice.employee_service.Mappers.PositionMapper;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Model.enums.ClassificationWorkday;
import microservice.employee_service.Repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    /**
     * Asynchronously create a new position.
     */
    @Override
    @Async
    @Transactional
    public void createPosition(PositionInsertDTO positionInsertDTO) {
        Position position = positionMapper.insertDtoToEntity(positionInsertDTO);
        positionRepository.saveAndFlush(position);
        logger.info("Position created: {}", position);
    }

    /**
     * Asynchronously get all positions.
     */
    @Override
    @Async
    public List<PositionDTO> getAllPositions() {
        List<Position> positions = positionRepository.findAll();
        return positions.stream()
                .map(positionMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Asynchronously get a position by ID.
     */
    @Override
    @Async
    public PositionDTO getPositionById(Long positionId) {
        Optional<Position> optionalPosition = positionRepository.findById(positionId);
        return optionalPosition.map(positionMapper::entityToDTO).orElse(null);
    }

    /**
     * Asynchronously update an existing position.
     */
    @Override
    @Async
    @Transactional
    public boolean updatePosition(PositionUpdateDTO positionUpdateDTO) {
        Optional<Position> positionOptional = positionRepository.findById(positionUpdateDTO.getId());
        if (positionOptional.isEmpty()) {
            logger.warn("Position with ID {} not found for update.", positionUpdateDTO.getId());
            return false;
        }
        Position position = positionOptional.get();

        position.setPositionName(positionUpdateDTO.getPositionName());
        position.setSalary(positionUpdateDTO.getSalary());
        position.setClassificationWorkday(ClassificationWorkday.valueOf(positionUpdateDTO.getClassificationWorkday()));
        position.setUpdatedAt(LocalDateTime.now());

        positionRepository.saveAndFlush(position);
        logger.info("Position updated: {}", position);
        return true;
    }

    /**
     * Asynchronously delete a position by ID.
     */
    @Override
    @Async
    @Transactional
    public void deletePosition(Long positionId) {
        positionRepository.deleteById(positionId);
        logger.info("Position with ID {} deleted.", positionId);
    }
}
