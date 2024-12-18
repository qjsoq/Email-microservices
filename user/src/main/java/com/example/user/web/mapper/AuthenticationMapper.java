package com.example.user.web.mapper;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.user.web.dto.AuthenticationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthenticationMapper {
    @Mapping(target = "expires", expression = "java(decodedJwt.getExpiresAt())")
    AuthenticationResponse toAuthResponse(DecodedJWT decodedJwt);
}
