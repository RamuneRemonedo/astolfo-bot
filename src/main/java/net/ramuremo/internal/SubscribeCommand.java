package net.ramuremo.internal;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface SubscribeCommand {
    String name();
}
