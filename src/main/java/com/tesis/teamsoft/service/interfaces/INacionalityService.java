package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.NationalityDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INacionalityService {

    NationalityDTO.NacionalityResponseDTO saveNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO);

    NationalityDTO.NacionalityResponseDTO updateNacionality(NationalityDTO.NacionalityCreateDTO nacionalityDTO, Long id);

    String deleteNacionality(Long id);

    List<NationalityDTO.NacionalityResponseDTO> findAllNacionality();

    List<NationalityDTO.NacionalityResponseDTO> findAllByOrderByIdAsc();

    NationalityDTO.NacionalityResponseDTO findNacionalityById(Long id);
}