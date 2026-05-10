package com.local.event.finder.user;

public interface UserService {

    UserResponseDto getUserById(Long id);

    UserUpdateResponseDto updateCurrentUser(UserUpdateRequestDto dto);

    void deleteCurrentUser();

}
