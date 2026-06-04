package com.java.eventify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.java.eventify.dto.EventCreateDTO;
import com.java.eventify.dto.EventResponseDTO;
import com.java.eventify.model.Event;
import com.java.eventify.model.Category;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Event toEntity(EventCreateDTO dto);

    @Mapping(target = "venueName", source = "venue.name")
    @Mapping(target = "categoryNames", expression = "java(mapCategoryNames(event))")
    EventResponseDTO toResponseDTO(Event event);

    default Set<String> mapCategoryNames(Event event){

        if(event.getCategories() == null){
            return Set.of();
        }

        return event.getCategories().stream()
        .map(Category::getName)
        .collect(Collectors.toSet());
    }

    

}
