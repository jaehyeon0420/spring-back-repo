package kr.or.iei.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME) //런타임까지 어노테이션 정보가 유지될 수 있도록
@Target(METHOD)     //메소드에만 적용하도록 
public @interface NoTokenCheck {

}
