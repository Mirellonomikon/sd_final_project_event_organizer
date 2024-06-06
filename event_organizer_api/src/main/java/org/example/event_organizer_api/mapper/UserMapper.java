package org.example.event_organizer_api.mapper;

import org.example.event_organizer_api.dto.user.*;
import org.example.event_organizer_api.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements GenericMapper<User, UserDTO>{

    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @Override
    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public UserIdDTO toIdDTO(User user) {
        return modelMapper.map(user, UserIdDTO.class);
    }
    public User signUpDtoToEntity(UserSignUpDTO dto) {
        User user = modelMapper.map(dto, User.class);
        user.setUserType(resolveUserType(dto.getUserTypeCode()));
        return user;
    }

    private String resolveUserType(String code) {
        return switch (code) {
            case "admin" -> "administrator";
            case "org" -> "organizer";
            default -> "client";
        };
    }
}
