package net.ilya.restcontrollerv100.mapper;

import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.entity.FileEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileDto map(FileEntity fileEntity);

    @InheritInverseConfiguration
    FileEntity map(FileDto fileDto);

}
