package com.xfs.user.mapper;

import com.xfs.user.pojo.dto.UserQuery;
import com.xfs.user.pojo.entity.User;
import com.xfs.user.pojo.vo.UserVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/* @Repository注解表明当前类是一个数据访问层组件,用于数据访问层的持久化操作(CRUD)
* 可以让spring框架自动管理此类对象,还能进行事务管理等服务 */
@Repository
public interface UserMapper {
    UserVO selectByUsername(String username);

    List<UserVO> selectUser(UserQuery userQuery);

    void insert(User user);

    void update(User user);

    void deleteById(Long userId);

    UserVO selectById(Long parentId);
}
