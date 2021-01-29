package snerble.minecraft.plugins.utils.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import snerble.minecraft.plugins.utils.templates.CommandBase;

/**
 * Enables automatic registration for {@link CommandBase} instances.
 * @author Conor
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface RegisterCommand {

}
