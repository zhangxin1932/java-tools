package com.zy.tools.spring.custom.annotation.rpc;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@RpcComponentScan
public @interface EnableRpc {

    /**
     * Base packages to scan for annotated @Service classes.
     * <p>
     * Use {@link #scanBasePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the base packages to scan
     * @see RpcComponentScan#basePackages()
     */
    @AliasFor(annotation = RpcComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    /**
     * Type-safe alternative to {@link #scanBasePackages()} for specifying the packages to
     * scan for annotated @Service classes. The package of each class specified will be
     * scanned.
     *
     * @return classes from the base packages to scan
     * @see RpcComponentScan#basePackageClasses
     */
    @AliasFor(annotation = RpcComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};
}
