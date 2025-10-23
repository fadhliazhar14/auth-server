package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.common.PageRequestDto;
import com.fadhli.auth_server.dto.common.PageResponseDto;
import com.fadhli.auth_server.dto.role.RoleMapper;
import com.fadhli.auth_server.dto.role.RoleRequestDto;
import com.fadhli.auth_server.dto.role.RoleResponseDto;
import com.fadhli.auth_server.entity.Role;
import com.fadhli.auth_server.exception.BusinessValidationException;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.RoleRepository;
import com.fadhli.auth_server.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public PageResponseDto<RoleResponseDto> findAll(PageRequestDto pageRequest) {
        Pageable pageable = PageUtil.createPageable(pageRequest);
        Page<Role> rolePage = roleRepository.findAllWithPagination(pageRequest.getSearch(), pageable);
        List<RoleResponseDto> dto = rolePage.stream()
                .map(roleMapper::toDto)
                .toList();

        return PageUtil.createPageResponse(dto, pageable, rolePage.getTotalElements());
    }

    public RoleResponseDto findById(Long id) {
        return roleMapper.toDto(
                roleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("Role")))
        );
    }

    public RoleResponseDto add(RoleRequestDto roleRequest) {
        isRoleNameExist(roleRequest.getName(), null);

        roleRequest.setIsActive(true);

        return roleMapper.toDto(
                roleRepository.save(roleMapper.toEntity(roleRequest))
        );
    }

    public RoleResponseDto edit(Long id, RoleRequestDto roleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("Role")));

        isRoleNameExist(roleRequest.getName(), id);

        roleRequest.setIsActive(role.getIsActive());
        roleMapper.updateFromDto(roleRequest, role);
        Role updatedRole = roleRepository.save(role);

        return roleMapper.toDto(updatedRole);
    }

    public void remove(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("Role")));

        role.setIsActive(false);

        roleRepository.save(role);
    }

    public void isRoleNameExist(String roleName, Long id) {
        if(id == null) {
            if (roleRepository.existsByName(roleName)) {
                throw new BusinessValidationException(ResponseMessages.taken("Role name"));
            }
        } else {
            if (roleRepository.existsByNameAndIdNot(roleName, id)) {
                throw new BusinessValidationException(ResponseMessages.taken("Email"));
            }
        }
    }
}