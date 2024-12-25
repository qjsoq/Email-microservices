package com.example.user.web.mapper;


import com.example.user.domain.User;
import com.example.user.web.dto.UserCreationDto;
import com.example.user.web.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUser(UserCreationDto dto);
    UserDto toDto(User user);
}
