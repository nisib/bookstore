package com.sulzer.bookstore.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

@Configuration
public class ModelMapperConfig {
    /**
     * Initializes {@link ModelMapper} with available {@link PropertyMap} instances.
     *
     * @param propertyMaps
     * @return
     */
    @Bean
    public ModelMapper modelMapper(List<PropertyMap> propertyMaps) {
        final ModelMapper modelMapper = new ModelMapper();
        propertyMaps.stream().filter(Objects::nonNull).forEach(modelMapper::addMappings);
        return modelMapper;
    }
}
