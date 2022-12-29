package cn.woodwhales.gray.server1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author woodwhales on 2022-12-28 19:57
 */
@EnableFeignClients(basePackages = {"cn.woodwhales.gray.common.feign"})
@ComponentScan({"cn.woodwhales.gray.**"})
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
