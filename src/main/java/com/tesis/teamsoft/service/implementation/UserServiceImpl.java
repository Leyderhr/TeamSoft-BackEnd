package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.UserEntity;
import com.tesis.teamsoft.persistence.entity.UserRoleEntity;
import com.tesis.teamsoft.persistence.repository.IUserRepository;
import com.tesis.teamsoft.persistence.repository.IUserRoleRepository;
import com.tesis.teamsoft.presentation.dto.UserDTO;
import com.tesis.teamsoft.presentation.dto.UserRoleDTO;
import com.tesis.teamsoft.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public UserDTO.UserResponseDTO saveUser(UserDTO.UserCreateDTO userDTO) {
            String username = generateUsername(userDTO.getPersonName(), userDTO.getSurname());
            String plainPassword = generatePassword(userDTO.getCard());

            UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
            userEntity.setUsername(username);
            userEntity.setPassword(passwordEncoder.encode(plainPassword));
            userEntity.setEnabled(userDTO.isEnabled());

            Set<UserRoleEntity> roles = new HashSet<>(userRoleRepository.findAllById(userDTO.getRoleIds()));

            if (roles.size() != userDTO.getRoleIds().size())
                throw new BusinessRuleException("One or more roles not found");

            userEntity.setRoles(roles);

            UserEntity savedUser = userRepository.save(userEntity);
            return convertToResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO.UserResponseDTO updateUser(UserDTO.UserCreateDTO userDTO, Long id) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        existingUser.setPersonName(userDTO.getPersonName());
        existingUser.setSurname(userDTO.getSurname());
        existingUser.setCard(userDTO.getCard());
        existingUser.setMail(userDTO.getMail());

        Set<UserRoleEntity> newRoles = new HashSet<>(userRoleRepository.findAllById(userDTO.getRoleIds()));

        if (newRoles.size() != userDTO.getRoleIds().size())
            throw new BusinessRuleException("One or more roles not found");

        existingUser.setRoles(newRoles);

        UserEntity updatedUser = userRepository.save(existingUser);
        return convertToResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public String deleteUser(Long id) {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

            userRepository.delete(user);
            return "User deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO.UserResponseDTO> findAllUsers() {
            return userRepository.findAll()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO.UserResponseDTO> findAllByOrderByIdAsc() {
            return userRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO.UserResponseDTO findUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO.UserResponseDTO findByMail(String email) {
        UserEntity user = userRepository.findByMail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToResponseDTO(user);
    }

    @Transactional
    public String resetPasswordToDefault(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        String defaultPassword = generatePassword(user.getCard());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
        refreshTokenService.deleteByUserId(userId);

        log.info("Password reset to default for user: {} (ID: {})", user.getUsername(), userId);
        return "Password reset successfully to system default";
    }

    private String generateUsername(String personName, String surname) {
        String username = "";
        String normalizedName = normalizeString((personName != null && !personName.trim().isEmpty()) ? personName : "");
        String normalizedSurname = normalizeString((surname !=null && !surname.trim().isEmpty()) ? surname : "");

        if (normalizedName.isEmpty() || normalizedSurname.isEmpty()) {
            throw new BusinessRuleException("Name and surname must have at least one part");
        }

        String[] lastname = extractFirstSurname(normalizedSurname);

        for (int i = 0; i < normalizedName.length(); i++) {
            username = normalizedName.substring(0, i + 1) + lastname[0];
            if (!usernameExists(username)) {
                return username;
            }
        }

        if (lastname[1] != null && !lastname[1].isEmpty()) {
            String normalizedSecondSurname = lastname[1];
            for (int i = 0; i < normalizedSecondSurname.length(); i++) {
                username = normalizedName.charAt(0) + lastname[0] +
                        normalizedSecondSurname.substring(0, i + 1);
                if (!usernameExists(username)) {
                    return username;
                }
            }
        }

        int counter = 1;
        String finalUsername = username + counter;
        while (usernameExists(finalUsername)) {
            counter++;
            finalUsername = username + counter;
        }

        return finalUsername;
    }

    private String[] extractFirstSurname(String surname) {
        if (surname == null || surname.trim().isEmpty())
            return new String[0];

        return surname.trim().split("\\s+"); // Primer apellido
    }

    private String normalizeString(String input) {
        if (input == null) return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        normalized = normalized.toLowerCase();

        normalized = normalized.replaceAll("[^a-z]", "");

        return normalized;
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private String generatePassword(String idCard) {
        return "ce" + idCard;
    }

    private UserDTO.UserResponseDTO convertToResponseDTO(UserEntity user) {
        UserDTO.UserResponseDTO responseDTO = modelMapper.map(user, UserDTO.UserResponseDTO.class);
        responseDTO.setIdCard(user.getCard());

        Set<UserRoleDTO.UserRoleResponseDTO> roleDTOs = user.getRoles().stream()
                .map(role -> modelMapper.map(role, UserRoleDTO.UserRoleResponseDTO.class))
                .collect(Collectors.toSet());

        responseDTO.setRoles(roleDTOs);
        return responseDTO;
    }
}