package cn.itcast.chatroom.repository;

import cn.itcast.chatroom.entity.User;

import java.util.List;

public interface UserRepository {
    public int save(User user);
    public int update(User user);
    public User findByUsername(String username);
    public int fCountByNameAndPw(User user);
    public String findIdByname(String id);
}
