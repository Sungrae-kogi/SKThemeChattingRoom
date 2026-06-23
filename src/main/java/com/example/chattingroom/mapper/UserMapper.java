package com.example.chattingroom.mapper;

import com.example.chattingroom.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int insertUser(UserEntity userEntity);
    UserEntity selectByUsername(String username);
}
