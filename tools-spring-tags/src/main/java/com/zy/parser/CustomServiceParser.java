package com.zy.parser;

import com.zy.parser.support.CustomServiceBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class CustomServiceParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String interfaceName = element.getAttribute("interfaceName");
        String ref = element.getAttribute("ref");
        String filterRef = element.getAttribute("filterRef");

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(CustomServiceBean.class);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
        beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
        beanDefinition.getPropertyValues().addPropertyValue("filterRef", filterRef);

        parserContext.getRegistry().registerBeanDefinition(interfaceName, beanDefinition);
        return beanDefinition;
    }
}
