package microservice.sale_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTO.Sale.CreateSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.ProcessSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleDTO;
import microservice.sale_service.Model.PhysicalSale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "saleDate", target = "saleDate")
    @Mapping(source = "saleStatus", target = "saleStatus")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "employeeId", target = "employeeId")
    List<SaleDTO> salesToDTOs(List<PhysicalSale> sales);

    @Mapping(source = "id", target = "saleId")
    @Mapping(source = "saleDate", target = "saleDate")
    @Mapping(source = "saleStatus", target = "saleStatus")
    @Mapping(source = "total", target = "totalAmount")
    @Mapping(source = "employeeId", target = "cashierId")
    @Mapping(expression = "java(sale.getEmployeeName())", target = "cashierName")
    @Mapping(source = "clientId", target = "clientId")
    CreateSaleDTO saleToCreateSaleDTO(PhysicalSale sale);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "saleDate", target = "saleDate")
    @Mapping(source = "saleStatus", target = "saleStatus")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "employeeId", target = "employeeId")
    ProcessSaleDTO paidSaleToProcessSaleDTO(PhysicalSale sale);
}