package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.repository.IUserRoleRepository;
import com.tesis.teamsoft.presentation.dto.UserRoleDTO;
import com.tesis.teamsoft.service.interfaces.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements IUserRoleService {

    private final IUserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleDTO.UserRoleResponseDTO> findAllByOrderByIdAsc() {
        return userRoleRepository.findAllByOrderByIdAsc()
                .stream()
                .map(role -> modelMapper.map(role, UserRoleDTO.UserRoleResponseDTO.class))
                .toList();
    }
}