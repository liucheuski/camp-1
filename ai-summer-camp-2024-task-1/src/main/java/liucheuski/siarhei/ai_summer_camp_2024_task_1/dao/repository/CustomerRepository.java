package liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.repository;


import liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
