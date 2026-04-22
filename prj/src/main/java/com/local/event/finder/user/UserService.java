package com.local.event.finder.user;

public interface UserService {

    UserResponseDto getUserById(Long id);

    UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto dto);

    void deleteUser(Long id);

}
