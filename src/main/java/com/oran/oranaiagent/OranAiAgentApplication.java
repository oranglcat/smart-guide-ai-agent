package com.oran.oranaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.oran.oranaiagent.mapper")
public class OranAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(OranAiAgentApplication.class, args);
    }

}
