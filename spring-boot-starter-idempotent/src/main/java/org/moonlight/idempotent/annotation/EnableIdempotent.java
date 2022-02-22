package org.moonlight.idempotent.annotation;

import org.moonlight.idempotent.config.IdempotentAutoConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * starter的两种方式:
 *    1. 在 resources 目录下新建 META-INF/spring.factories 添加 org.springframework.boot.autoconfigure.EnableAutoConfiguration=自动装配配置类全路径
 *    2. 像这样子起一个注解 上面加上 @Import(自动装配配置类.class)，使用的时候在 工程启动类上 添加上该注解即可
 * @author Moonlight
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(IdempotentAutoConfigure.class)
public @interface EnableIdempotent {
    
}