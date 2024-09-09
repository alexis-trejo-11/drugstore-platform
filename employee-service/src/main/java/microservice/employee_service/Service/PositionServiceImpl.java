package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionUpdateDTO;
import microservice.employee_service.Mappers.PositionMapper;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Model.enums.ClassificationWorkday;
import microservice.employee_service.Repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    @Override
    @Transactional
    public void createPosition(PositionInsertDTO positionInsertDTO) {
        Position position = positionMapper.insertDtoToEntity(positionInsertDTO);
        positionRepository.saveAndFlush(position);
    }

    @Override
    public List<PositionDTO> getAllPositions() {
        List<Position> positions = positionRepository.findAll();
        return positions.stream()
                .map(positionMapper::entityToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public PositionDTO getPositionById(Long positionId) {
        Optional<Position> optionalPosition = positionRepository.findById(positionId);
        return optionalPosition.map(positionMapper::entityToDTO).orElse(null);
    }


    @Override
    @Transactional
    public void updatePosition(PositionUpdateDTO positionUpdateDTO) {
        Position position = positionRepository.findById(positionUpdateDTO.getId()).orElse(null);
        if (position == null) { return; }

        positionMapper.updateDTOtoEntity(positionUpdateDTO, position);

        positionRepository.saveAndFlush(position);
    }

    @Override
    @Transactional
    public void deletePosition(Long positionId) {
        positionRepository.deleteById(positionId);
    }

    @Override
    public boolean validateExisitingPosition(Long positionId) {
        return positionRepository.findById(positionId).isPresent();
    }
}
