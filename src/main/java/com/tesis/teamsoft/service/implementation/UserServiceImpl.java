package com.tesis.teamsoft.service.implementation;

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
    public UserDTO.UserResponseDTO saveUser(UserDTO.UserCreateDTO userDTO) {
        try {
            // Generar username automáticamente
            String username = generateUsername(
                    userDTO.getPersonName(),
                    userDTO.getSurname()
            );

            // Generar password automáticamente
            String plainPassword = generatePassword(userDTO.getIdCard());

            // Crear la entidad del usuario
            UserEntity userEntity = new UserEntity();
            userEntity.setPersonName(userDTO.getPersonName());
            userEntity.setSurname(userDTO.getSurname());
            userEntity.setIdCard(userDTO.getIdCard());
            userEntity.setMail(userDTO.getMail());
            userEntity.setUsername(username);
            userEntity.setPassword(passwordEncoder.encode(plainPassword));
            userEntity.setEnabled(userDTO.isEnabled());

            // Asignar roles
            Set<UserRoleEntity> roles = new HashSet<>(userRoleRepository.findAllById(userDTO.getRoleIds()));

            if (roles.size() != userDTO.getRoleIds().size()) {
                throw new IllegalArgumentException("One or more roles not found");
            }

            userEntity.setRoles(roles);

            // Guardar usuario
            UserEntity savedUser = userRepository.save(userEntity);
            return convertToResponseDTO(savedUser);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public UserDTO.UserResponseDTO updateUser(UserDTO.UserCreateDTO userDTO, Long id) {
        try {
            // Verificar que el usuario existe
            UserEntity existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

            // Validar unicidad (excluyendo el usuario actual)
            //validateUniqueConstraints(userDTO.getUsername(), userDTO.getMail(),
            //        userDTO.getIdCard(), id);

            // Actualizar campos
            existingUser.setPersonName(userDTO.getPersonName());
            existingUser.setSurname(userDTO.getSurname());
            existingUser.setIdCard(userDTO.getIdCard());
            existingUser.setMail(userDTO.getMail());
            //existingUser.setUsername(userDTO.getUsername());
            existingUser.setEnabled(userDTO.isEnabled());

            // Actualizar contraseña si se proporcionó
//            if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
//                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//            }

            // Actualizar roles
            Set<UserRoleEntity> roles = new HashSet<>(userRoleRepository.findAllById(userDTO.getRoleIds()));

            if (roles.size() != userDTO.getRoleIds().size()) {
                throw new IllegalArgumentException("One or more roles not found");
            }

            existingUser.setRoles(roles);

            // Guardar cambios
            UserEntity updatedUser = userRepository.save(existingUser);
            return convertToResponseDTO(updatedUser);

//        } catch (IllegalArgumentException | RuntimeException e) {
//            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public String deleteUser(Long id) {
        try {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

            // Puedes agregar validaciones adicionales antes de eliminar
            userRepository.delete(user);
            return "User deleted successfully";

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public List<UserDTO.UserResponseDTO> findAllUsers() {
        try {
            return userRepository.findAll()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all users: " + e.getMessage());
        }
    }

    @Override
    public List<UserDTO.UserResponseDTO> findAllByOrderByIdAsc() {
        try {
            return userRepository.findAllByOrderByIdAsc()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error finding all users ordered by ID: " + e.getMessage());
        }
    }

    @Override
    public UserDTO.UserResponseDTO findUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return convertToResponseDTO(user);
    }

    @Override
    public UserDTO.UserResponseDTO findByMail(String email) {
        UserEntity user = userRepository.findByMail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToResponseDTO(user);
    }

    @Transactional
    public String resetPasswordToDefault(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        String defaultPassword = generatePassword(user.getIdCard());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
        refreshTokenService.deleteByUserId(userId);

        log.info("Password reset to default for user: {} (ID: {})", user.getUsername(), userId);
        return "Password reset successfully to system default";
    }



    private String generateUsername(String personName, String surname) {
        String username = "";
        String name = (personName != null && !personName.trim().isEmpty()) ? personName : "";

        // Extraer el primer apellido de surname
        String[] lastname = extractFirstSurname(surname);

        // Normalizar strings
        String normalizedFirstName = normalizeString(name);
        String normalizedFirstSurname = normalizeString(lastname[0]);

        if (normalizedFirstName.isEmpty() || normalizedFirstSurname.isEmpty()) {
            throw new IllegalArgumentException("Name and surname must have at least one part");
        }

        // Si existe, probar con las siguientes letras del primer nombre
        for (int i = 0; i < normalizedFirstName.length(); i++) {
            username = normalizedFirstName.substring(0, i + 1) + normalizedFirstSurname;
            if (!usernameExists(username)) {
                return username;
            }
        }

        // Si aún existe, intentar con el segundo apellido (si existe)
        if (lastname[1] != null && !lastname[1].isEmpty()) {
            String normalizedSecondSurname = normalizeString(lastname[1]);
            for (int i = 0; i < normalizedSecondSurname.length(); i++) {
                username = normalizedFirstName.charAt(0) + normalizedFirstSurname +
                        normalizedSecondSurname.substring(0, i + 1);
                if (!usernameExists(username)) {
                    return username;
                }
            }
        }

        // Como último recurso, agregar un número
        int counter = 1;
        String finalUsername = username + counter;
        while (usernameExists(finalUsername)) {
            counter++;
            finalUsername = username + counter;
        }

        return finalUsername;
    }

    /**
     * Extrae el primer nombre de personName (puede contener uno o dos nombres)
     */
    private String extractFirstName(String personName) {
        if (personName == null || personName.trim().isEmpty()) {
            return "";
        }
        String[] parts = personName.trim().split("\\s+");
        return parts[0]; // Primer nombre
    }

    /**
     * Extrae el segundo nombre de personName si existe
     */
    private String extractSecondName(String personName) {
        if (personName == null || personName.trim().isEmpty()) {
            return null;
        }
        String[] parts = personName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : null;
    }

    /**
     * Extrae el primer apellido de surname (puede contener uno o dos apellidos)
     */
    private String[] extractFirstSurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) {
            return null;
        }
        return surname.trim().split("\\s+"); // Primer apellido
    }

    /**
     * Extrae el segundo apellido de surname si existe
     */
    private String extractSecondSurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) {
            return null;
        }
        String[] parts = surname.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : null;
    }

    /**
     * Normaliza una cadena: quita tildes, convierte a minúsculas y elimina caracteres no deseados
     */
    private String normalizeString(String input) {
        if (input == null) return "";

        // Quitar tildes
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Convertir a minúsculas
        normalized = normalized.toLowerCase();

        // Eliminar espacios, guiones y caracteres especiales (mantener solo letras)
        normalized = normalized.replaceAll("[^a-z]", "");

        return normalized;
    }

    /**
     * Verifica si un username ya existe en la base de datos
     */
    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Genera la contraseña según la política: "ce" + idCard
     */
    private String generatePassword(String idCard) {
        return "ce" + idCard;
    }

    private UserDTO.UserResponseDTO convertToResponseDTO(UserEntity user) {
        UserDTO.UserResponseDTO responseDTO = modelMapper.map(user, UserDTO.UserResponseDTO.class);

        // Convertir roles a DTOs
        Set<UserRoleDTO.UserRoleResponseDTO> roleDTOs = user.getRoles().stream()
                .map(role -> modelMapper.map(role, UserRoleDTO.UserRoleResponseDTO.class))
                .collect(Collectors.toSet());

        responseDTO.setRoles(roleDTOs);
        return responseDTO;
    }
}