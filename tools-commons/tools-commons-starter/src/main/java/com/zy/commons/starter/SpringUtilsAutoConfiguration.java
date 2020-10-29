package com.zy.commons.starter;

import com.zy.commons.lang.utils.SpringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringUtilsAutoConfiguration {
    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }
}
