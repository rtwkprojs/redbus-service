package com.redbus.user.mapper;

import com.redbus.user.dto.UserResponseDto;
import com.redbus.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    UserResponseDto toResponseDto(User user);
}
