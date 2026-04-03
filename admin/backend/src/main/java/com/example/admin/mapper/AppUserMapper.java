package com.example.admin.mapper;

import com.example.admin.model.AppUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AppUserMapper {

    @Select("""
            <script>
            select id, username, password_hash, nickname, phone, email, status, last_login_at, created_at, updated_at
            from `user`
            <where>
                <if test="keyword != null and keyword != ''">
                    (
                        username like concat('%', #{keyword}, '%')
                        or nickname like concat('%', #{keyword}, '%')
                        or phone like concat('%', #{keyword}, '%')
                        or email like concat('%', #{keyword}, '%')
                    )
                </if>
            </where>
            order by created_at desc, id desc
            </script>
            """)
    List<AppUser> findAll(@Param("keyword") String keyword);

    @Select("select id, username, password_hash, nickname, phone, email, status, last_login_at, created_at, updated_at from `user` where id = #{id} limit 1")
    AppUser findById(@Param("id") Long id);

    @Select("select id, username, password_hash, nickname, phone, email, status, last_login_at, created_at, updated_at from `user` where username = #{username} limit 1")
    AppUser findByUsername(@Param("username") String username);

    @Select("""
            <script>
            select count(1)
            from `user`
            where phone = #{phone}
            <if test="excludeId != null">
                and id &lt;&gt; #{excludeId}
            </if>
            </script>
            """)
    int countByPhone(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    @Select("""
            <script>
            select count(1)
            from `user`
            where email = #{email}
            <if test="excludeId != null">
                and id &lt;&gt; #{excludeId}
            </if>
            </script>
            """)
    int countByEmail(@Param("email") String email, @Param("excludeId") Long excludeId);

    @Insert("""
            insert into `user`(username, password_hash, nickname, phone, email, status, created_at, updated_at)
            values(#{username}, #{passwordHash}, #{nickname}, #{phone}, #{email}, #{status}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppUser appUser);

    @Update("""
            <script>
            update `user`
            <set>
                nickname = #{nickname},
                phone = #{phone},
                email = #{email},
                status = #{status},
                updated_at = now(),
                <if test="passwordHash != null">
                    password_hash = #{passwordHash},
                </if>
            </set>
            where id = #{id}
            </script>
            """)
    int update(AppUser appUser);

    @Delete("delete from `user` where id = #{id}")
    int deleteById(@Param("id") Long id);
}
