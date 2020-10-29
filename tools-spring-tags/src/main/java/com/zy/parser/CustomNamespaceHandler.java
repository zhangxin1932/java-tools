package com.zy.parser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CustomNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("service", new CustomServiceParser());
    }
}
