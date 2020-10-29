package com.zy.tools.spring.custom.annotation.rpc;

import com.zy.tools.spring.custom.annotation.support.RpcImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RpcImportBeanDefinitionRegistrar.class)
public @interface RpcComponentScan {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @RpcComponentScan("org.my.pkg")} instead of
     * {@code @RpcComponentScan(basePackages="org.my.pkg")}.
     *
     * @return the base packages to scan
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated @Service classes. {@link #value()} is an
     * alias for (and mutually exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the base packages to scan
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for annotated @Service classes. The package of each class specified will be
     * scanned.
     *
     * @return classes from the base packages to scan
     */
    Class<?>[] basePackageClasses() default {};
}
