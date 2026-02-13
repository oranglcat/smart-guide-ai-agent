package com.oran.oranaiagent.tools;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oran.oranaiagent.entity.User;
import com.oran.oranaiagent.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

public class DatabaseTool {

    private final UserMapper userMapper;

    public DatabaseTool(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    @Tool(description = "add user information to database")
    public void addUser(@ToolParam(description = "Table fields") String name,@ToolParam(description = "Table fields") Integer age, @ToolParam(description = "Table fields")String email){
        User user = new User();
        user.setAge(age);
        user.setEmail(email);
        user.setName(name);
        userMapper.insert(user);
    }

    @Tool(description = "query user information from database")
    public String QueryUser(@ToolParam(description = "Table fields") String name){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name",name);
        List<User> userList = userMapper.selectList(wrapper);
        String userListString = userList.toString();
        return userListString;
    }

}
