package com.example.admin.mapper;

import com.example.admin.model.AdminUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface AdminUserMapper {

    @Select("select * from admin_user where username = #{username} limit 1")
    AdminUser findByUsername(String username);

    @Insert("""
            insert into admin_user(username, password_hash, nickname, phone, email, status, created_at, updated_at)
            values(#{username}, #{passwordHash}, #{nickname}, #{phone}, #{email}, #{status}, #{createdAt}, #{updatedAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminUser adminUser);

    @Update("update admin_user set last_login_at = #{lastLoginAt}, updated_at = #{lastLoginAt} where id = #{id}")
    int updateLastLoginAt(Long id, LocalDateTime lastLoginAt);
}
