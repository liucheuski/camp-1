package liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Customers")
public record Customer(@Id Long chatId, String treadId) {
}
