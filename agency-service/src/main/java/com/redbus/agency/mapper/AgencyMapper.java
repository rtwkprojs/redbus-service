package com.redbus.agency.mapper;

import com.redbus.agency.dto.AgencyResponseDto;
import com.redbus.agency.entity.Agency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgencyMapper {
    
    @Mapping(target = "vehicleCount", expression = "java(agency.getVehicles() != null ? agency.getVehicles().size() : 0)")
    AgencyResponseDto toResponseDto(Agency agency);
}
