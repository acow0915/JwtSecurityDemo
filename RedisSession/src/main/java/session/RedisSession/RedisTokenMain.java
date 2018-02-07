package session.RedisSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages={"com"})
public class RedisTokenMain 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(RedisTokenMain.class, args);
    }
}
