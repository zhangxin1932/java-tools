package com.zy.tools.undefined.spring.custom.annotation.support;

import com.zy.tools.undefined.spring.custom.annotation.rpc.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class RpcServiceAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware, BeanClassLoaderAware {

    private final Set<String> packages2Scan;

    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public RpcServiceAnnotationBeanPostProcessor(Set<String> packages2Scan) {
        this.packages2Scan = packages2Scan;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvePackages2Scan = resolvePackages2Scan(packages2Scan);

        if (CollectionUtils.isEmpty(resolvePackages2Scan)) {
            // todo populate some logs
        } else {
            registerRpcServiceBeans(resolvePackages2Scan, registry);
        }
    }

    private void registerRpcServiceBeans(Set<String> packages2Scan, BeanDefinitionRegistry registry) {
        RpcClassPathBeanDefinitionScanner scanner = new RpcClassPathBeanDefinitionScanner(registry, false, environment, resourceLoader);

        AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
        scanner.setBeanNameGenerator(beanNameGenerator);

        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));

        for (String package2Scan : packages2Scan) {
            // Registers @RpcService Bean first
            scanner.scan(package2Scan);

            // Finds all BeanDefinitionHolders of @RpcService whether @ComponentScan scans or not.
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(package2Scan);
            Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (Objects.isNull(beanDefinition)) {
                    continue;
                }
                String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                // register all @RpcService
                registerRpcServiceBean(beanDefinitionHolder, registry, scanner);
            }
        }
    }

    private void registerRpcServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry, RpcClassPathBeanDefinitionScanner scanner) {
        Class<?> beanClass = ClassUtils.resolveClassName(beanDefinitionHolder.getBeanDefinition().getBeanClassName(), classLoader);
        RpcService rpcService = AnnotationUtils.findAnnotation(beanClass, RpcService.class);

        // TODO 这里需要补充一些内容
    }

    private Set<String> resolvePackages2Scan(Set<String> packagesToScan) {
        Set<String> resolvePackages2Scan = new LinkedHashSet<>(packagesToScan.size());
        for (String package2Scan : packagesToScan) {
            if (StringUtils.hasText(package2Scan)) {
                String placeholders = environment.resolvePlaceholders(package2Scan.trim());
                resolvePackages2Scan.add(placeholders);
            }
        }
        return resolvePackages2Scan;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // just skip, right now.
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
