package com.fadhli.auth_server.dto.token;

import com.fadhli.auth_server.dto.keypair.JwkResponseDto;
import com.fadhli.auth_server.entity.JwksKey;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JwkMapper {
    List<JwkResponseDto.JwkKey> toJwkKeyDtoList(List<JwksKey> jwkKeys);
}
