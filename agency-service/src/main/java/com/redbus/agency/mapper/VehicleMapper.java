package com.redbus.agency.mapper;

import com.redbus.agency.dto.VehicleResponseDto;
import com.redbus.agency.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {
    
    @Mapping(source = "agency.referenceId", target = "agencyReferenceId")
    @Mapping(source = "agency.agencyName", target = "agencyName")
    VehicleResponseDto toResponseDto(Vehicle vehicle);
}
