package com.oran.oranaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oran.oranaiagent.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 后，已拥有 CRUD 方法
}
