package com.zy.tools.spring.custom.annotation.support;

import com.zy.tools.spring.custom.annotation.rpc.RpcComponentScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RpcImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> packages2Scan = packages2Scan(importingClassMetadata);
        registerRpcServiceAnnotationBeanPostProcessor(packages2Scan, registry);
    }

    private void registerRpcServiceAnnotationBeanPostProcessor(Set<String> packages2Scan, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RpcServiceAnnotationBeanPostProcessor.class);
        builder.addConstructorArgValue(packages2Scan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private Set<String> packages2Scan(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcComponentScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        String[] values = attributes.getStringArray("value");

        Set<String> packages2Scan = new LinkedHashSet<>(Arrays.asList(values));
        packages2Scan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packages2Scan.add(ClassUtils.getPackageName(basePackageClass));
        }

        return packages2Scan.isEmpty() ? Collections.singleton(ClassUtils.getPackageName(importingClassMetadata.getClassName())) : packages2Scan;

    }
}
