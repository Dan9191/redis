package ru.dan.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.dan.redis.serialize.JacksonSerializeService;

@Slf4j
@SpringBootApplication
@AllArgsConstructor
public class RedisApplication implements CommandLineRunner {

    private final JacksonSerializeService jacksonSerializeService;


    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication springApplication = new SpringApplication(RedisApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        log.info("EXECUTING : command line runner");
        jacksonSerializeService.serialize();
    }
}