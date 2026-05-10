package com.local.event.finder.user;

public interface UserService {

    UserResponseDto getUserById(Long id);

    UserUpdateResponseDto updateUserCurrent(UserUpdateRequestDto dto);

    void deleteUser(Long id);

}
