package com.redbus.agency.service.impl;

import com.redbus.agency.dto.AgencyRequestDto;
import com.redbus.agency.dto.AgencyResponseDto;
import com.redbus.agency.dto.VehicleRequestDto;
import com.redbus.agency.dto.VehicleResponseDto;
import com.redbus.agency.entity.Agency;
import com.redbus.agency.entity.Vehicle;
import com.redbus.agency.mapper.AgencyMapper;
import com.redbus.agency.mapper.VehicleMapper;
import com.redbus.agency.repository.AgencyRepository;
import com.redbus.agency.repository.VehicleRepository;
import com.redbus.agency.service.AgencyService;
import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgencyServiceImpl implements AgencyService {
    
    private final AgencyRepository agencyRepository;
    private final VehicleRepository vehicleRepository;
    private final AgencyMapper agencyMapper;
    private final VehicleMapper vehicleMapper;
    
    @Override
    public AgencyResponseDto createAgency(AgencyRequestDto requestDto, String ownerReferenceId) {
        log.info("Creating agency: {} for owner: {}", requestDto.getAgencyName(), ownerReferenceId);
        
        // Check if email or phone already exists
        if (agencyRepository.existsByContactEmail(requestDto.getContactEmail())) {
            throw new BusinessException("Agency with email already exists: " + requestDto.getContactEmail());
        }
        
        if (agencyRepository.existsByContactPhone(requestDto.getContactPhone())) {
            throw new BusinessException("Agency with phone already exists: " + requestDto.getContactPhone());
        }
        
        Agency agency = new Agency();
        agency.setAgencyName(requestDto.getAgencyName());
        agency.setContactEmail(requestDto.getContactEmail());
        agency.setContactPhone(requestDto.getContactPhone());
        agency.setAddress(requestDto.getAddress());
        agency.setOwnerReferenceId(ownerReferenceId);
        agency.setIsActive(true);
        
        agency = agencyRepository.save(agency);
        log.info("Agency created with referenceId: {}", agency.getReferenceId());
        
        return agencyMapper.toResponseDto(agency);
    }
    
    @Override
    public AgencyResponseDto updateAgency(UUID referenceId, AgencyRequestDto requestDto, String ownerReferenceId) {
        log.info("Updating agency: {}", referenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(referenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        // Check if email is being changed and already exists
        if (!agency.getContactEmail().equals(requestDto.getContactEmail()) 
                && agencyRepository.existsByContactEmail(requestDto.getContactEmail())) {
            throw new BusinessException("Email already in use: " + requestDto.getContactEmail());
        }
        
        // Check if phone is being changed and already exists
        if (!agency.getContactPhone().equals(requestDto.getContactPhone())
                && agencyRepository.existsByContactPhone(requestDto.getContactPhone())) {
            throw new BusinessException("Phone already in use: " + requestDto.getContactPhone());
        }
        
        agency.setAgencyName(requestDto.getAgencyName());
        agency.setContactEmail(requestDto.getContactEmail());
        agency.setContactPhone(requestDto.getContactPhone());
        agency.setAddress(requestDto.getAddress());
        
        agency = agencyRepository.save(agency);
        return agencyMapper.toResponseDto(agency);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AgencyResponseDto getAgencyByReferenceId(UUID referenceId) {
        log.info("Fetching agency: {}", referenceId);
        
        Agency agency = agencyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
        
        return agencyMapper.toResponseDto(agency);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AgencyResponseDto> getAgenciesByOwner(String ownerReferenceId) {
        log.info("Fetching agencies for owner: {}", ownerReferenceId);
        
        return agencyRepository.findByOwnerReferenceId(ownerReferenceId)
                .stream()
                .map(agencyMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AgencyResponseDto> getAllActiveAgencies() {
        log.info("Fetching all active agencies");
        
        return agencyRepository.findByIsActiveTrue()
                .stream()
                .map(agencyMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteAgency(UUID referenceId, String ownerReferenceId) {
        log.info("Deleting agency: {}", referenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(referenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        agencyRepository.delete(agency);
        log.info("Agency deleted: {}", referenceId);
    }
    
    @Override
    public void activateAgency(UUID referenceId, String ownerReferenceId) {
        log.info("Activating agency: {}", referenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(referenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        agency.setIsActive(true);
        agencyRepository.save(agency);
    }
    
    @Override
    public void deactivateAgency(UUID referenceId, String ownerReferenceId) {
        log.info("Deactivating agency: {}", referenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(referenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        agency.setIsActive(false);
        agencyRepository.save(agency);
    }
    
    @Override
    public VehicleResponseDto addVehicle(UUID agencyReferenceId, VehicleRequestDto requestDto, String ownerReferenceId) {
        log.info("Adding vehicle to agency: {}", agencyReferenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(agencyReferenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        if (vehicleRepository.existsByRegistrationNumber(requestDto.getRegistrationNumber())) {
            throw new BusinessException("Vehicle with registration number already exists: " + requestDto.getRegistrationNumber());
        }
        
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(requestDto.getRegistrationNumber());
        vehicle.setVehicleType(requestDto.getVehicleType());
        vehicle.setTotalSeats(requestDto.getTotalSeats());
        vehicle.setManufacturer(requestDto.getManufacturer());
        vehicle.setModel(requestDto.getModel());
        vehicle.setYearOfManufacture(requestDto.getYearOfManufacture());
        vehicle.setHasAC(requestDto.getHasAC());
        vehicle.setHasWifi(requestDto.getHasWifi());
        vehicle.setHasChargingPoints(requestDto.getHasChargingPoints());
        vehicle.setIsActive(true);
        vehicle.setAgency(agency);
        
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle added with referenceId: {}", vehicle.getReferenceId());
        
        return vehicleMapper.toResponseDto(vehicle);
    }
    
    @Override
    public VehicleResponseDto updateVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, 
                                           VehicleRequestDto requestDto, String ownerReferenceId) {
        log.info("Updating vehicle: {} in agency: {}", vehicleReferenceId, agencyReferenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(agencyReferenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        Vehicle vehicle = vehicleRepository.findByReferenceIdAndAgency(vehicleReferenceId, agencyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found in this agency"));
        
        // Check if registration number is being changed and already exists
        if (!vehicle.getRegistrationNumber().equals(requestDto.getRegistrationNumber())
                && vehicleRepository.existsByRegistrationNumber(requestDto.getRegistrationNumber())) {
            throw new BusinessException("Registration number already exists: " + requestDto.getRegistrationNumber());
        }
        
        vehicle.setRegistrationNumber(requestDto.getRegistrationNumber());
        vehicle.setVehicleType(requestDto.getVehicleType());
        vehicle.setTotalSeats(requestDto.getTotalSeats());
        vehicle.setManufacturer(requestDto.getManufacturer());
        vehicle.setModel(requestDto.getModel());
        vehicle.setYearOfManufacture(requestDto.getYearOfManufacture());
        vehicle.setHasAC(requestDto.getHasAC());
        vehicle.setHasWifi(requestDto.getHasWifi());
        vehicle.setHasChargingPoints(requestDto.getHasChargingPoints());
        
        vehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(vehicle);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDto> getVehiclesByAgency(UUID agencyReferenceId) {
        log.info("Fetching vehicles for agency: {}", agencyReferenceId);
        
        return vehicleRepository.findByAgency_ReferenceId(agencyReferenceId)
                .stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDto getVehicleByReferenceId(UUID vehicleReferenceId) {
        log.info("Fetching vehicle: {}", vehicleReferenceId);
        
        Vehicle vehicle = vehicleRepository.findByReferenceId(vehicleReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        
        return vehicleMapper.toResponseDto(vehicle);
    }
    
    @Override
    public void deleteVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, String ownerReferenceId) {
        log.info("Deleting vehicle: {} from agency: {}", vehicleReferenceId, agencyReferenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(agencyReferenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        Vehicle vehicle = vehicleRepository.findByReferenceIdAndAgency(vehicleReferenceId, agencyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found in this agency"));
        
        vehicleRepository.delete(vehicle);
        log.info("Vehicle deleted: {}", vehicleReferenceId);
    }
    
    @Override
    public void activateVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, String ownerReferenceId) {
        log.info("Activating vehicle: {} in agency: {}", vehicleReferenceId, agencyReferenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(agencyReferenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        Vehicle vehicle = vehicleRepository.findByReferenceIdAndAgency(vehicleReferenceId, agencyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found in this agency"));
        
        vehicle.setIsActive(true);
        vehicleRepository.save(vehicle);
    }
    
    @Override
    public void deactivateVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, String ownerReferenceId) {
        log.info("Deactivating vehicle: {} in agency: {}", vehicleReferenceId, agencyReferenceId);
        
        Agency agency = agencyRepository.findByReferenceIdAndOwner(agencyReferenceId, ownerReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found or you don't have permission"));
        
        Vehicle vehicle = vehicleRepository.findByReferenceIdAndAgency(vehicleReferenceId, agencyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found in this agency"));
        
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }
}
