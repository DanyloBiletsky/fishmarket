package technikal.task.fishmarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technikal.task.fishmarket.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
}
