package fr.dush.mediamanager.annotations.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/** Field annotated with it will be created but never updated. */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SetOnInsert {

}
