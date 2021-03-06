package lodsve.cache.annotations;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用cache模块.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 2016/1/19 15:10
 * @see CacheConfigurationSelector
 * @see CacheMode
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CacheConfigurationSelector.class})
public @interface EnableCache {
    /**
     * 选择使用的缓存类型
     *
     * @return
     * @see CacheMode
     */
    CacheMode cache() default CacheMode.REDIS;
}
