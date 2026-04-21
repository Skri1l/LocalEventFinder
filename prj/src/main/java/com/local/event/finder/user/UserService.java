package com.local.event.finder.user;

public interface UserService {

    UserResponseDto getUserById(Long id);

    UserUpdateResponseDto updateUser(Long id, UserRequestDto dto);

    void deleteUser(Long id);

}
