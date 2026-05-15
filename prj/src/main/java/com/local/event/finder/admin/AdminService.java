package com.local.event.finder.admin;

public interface AdminService {

    void blockUser(Long id);

    void unblockUser(Long id);

    void makeUserAdmin(Long id);
}
