package tech.zg.research.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * field 类型注解
 *
 * @version V1.0.0
 * @author: 张弓
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TextFieldMark {

    int store() default -1;
}
