package com.example.user.web.mapper;


import com.example.user.domain.User;
import com.example.user.web.dto.UserCreationDto;
import com.example.user.web.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User toUser(UserCreationDto dto);

    UserDto toDto(User user);

}
