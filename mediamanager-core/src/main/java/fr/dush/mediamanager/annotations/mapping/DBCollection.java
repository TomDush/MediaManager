package fr.dush.mediamanager.annotations.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/** Define target collection name for persisted entity. */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBCollection {

    String value() default "";

}
