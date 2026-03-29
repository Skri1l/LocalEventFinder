package com.local.event.finder.service;

import com.local.event.finder.model.dto.UserRequestDto;
import com.local.event.finder.model.dto.UserResponseDto;
import com.local.event.finder.model.entity.User;

public interface UserService {

    UserResponseDto register(UserRequestDto userDto);
}
