package com.java.eventify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.java.eventify.dto.VenueCreateDTO;
import com.java.eventify.dto.VenueResponseDTO;
import com.java.eventify.model.Venue;

@Mapper(componentModel = "spring")
public interface VennueMapper {
    
    @Mapping(target = "id", ignore = true)
    Venue toEntity(VenueCreateDTO dto);

    VenueResponseDTO toResponseDTO(Venue venue);
    

}
