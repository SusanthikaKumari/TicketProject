package com.susanthika.TicketProject.config;

import com.susanthika.TicketProject.dto.request.UserUpdateRequest;
import com.susanthika.TicketProject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    void update(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

}
