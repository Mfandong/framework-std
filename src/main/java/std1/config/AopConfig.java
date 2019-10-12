package std1.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan({"std1"})
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AopConfig {

}
