package liucheuski.siarhei.ai_summer_camp_2024_task_1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    public static final String REDIS_HOST = "redisHost";
    public static final String REDIS_PORT = "redisPort";
    private static final String REDIS_PASSWORD = "redisPassword";

    @Bean
    public LettuceConnectionFactory connectionFactory(Environment environment) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(environment.getProperty(REDIS_HOST),
                Integer.parseInt(environment.getProperty(REDIS_PORT)));
        configuration.setPassword(environment.getProperty(REDIS_PASSWORD));
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
