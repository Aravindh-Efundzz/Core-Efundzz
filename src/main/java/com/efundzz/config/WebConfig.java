package com.efundzz.config;

import com.efundzz.utils.LoanTypeConverter;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Autowired
    ListableBeanFactory beanFactory;
    @Override
    public void addFormatters(FormatterRegistry registry) {registry.addConverter(new LoanTypeConverter());}
}
