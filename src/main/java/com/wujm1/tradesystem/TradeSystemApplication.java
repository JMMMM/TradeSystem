package com.wujm1.tradesystem;

import com.wujm1.tradesystem.config.RestTemplateConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"com.wujm1.tradesystem.mapper"})
@Import({RestTemplateConfiguration.class})
public class TradeSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TradeSystemApplication.class, args);
    }
    
}
