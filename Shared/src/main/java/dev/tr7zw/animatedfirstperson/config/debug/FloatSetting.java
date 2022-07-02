package dev.tr7zw.animatedfirstperson.config.debug;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface FloatSetting {

    public float min();
    public float max();
    public float step();
}
