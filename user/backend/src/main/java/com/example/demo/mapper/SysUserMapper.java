package com.example.demo.mapper;

import com.example.demo.model.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface SysUserMapper {

    @Select("SELECT id, username, password_hash, nickname, phone, email, status, last_login_at, created_at, updated_at " +
            ", role " +
            "FROM `user` WHERE username = #{username} LIMIT 1")
    SysUser findByUsername(@Param("username") String username);

    @Select("SELECT id, username, password_hash, nickname, phone, email, status, last_login_at, created_at, updated_at, role " +
            "FROM `user` WHERE id = #{id} LIMIT 1")
    SysUser findById(@Param("id") Long id);

    @Select("SELECT COUNT(1) FROM `user` WHERE phone = #{phone}")
    int countByPhone(@Param("phone") String phone);

    @Select("SELECT COUNT(1) FROM `user` WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    @Insert("INSERT INTO `user` (username, password_hash, nickname, phone, email, role, status, created_at, updated_at) " +
            "VALUES (#{username}, #{passwordHash}, #{nickname}, #{phone}, #{email}, #{role}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("UPDATE `user` SET last_login_at = #{lastLoginAt}, updated_at = NOW() WHERE id = #{id}")
    int updateLastLoginAt(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);

    @Select("SELECT COUNT(1) FROM `user` WHERE role = #{role} AND status = 1")
    int countByRole(@Param("role") String role);

    @Select("SELECT id FROM `user` WHERE role = #{role} AND status = 1 ORDER BY created_at ASC, id ASC LIMIT 1")
    Long findFirstIdByRole(@Param("role") String role);
}
