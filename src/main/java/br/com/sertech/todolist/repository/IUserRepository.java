package br.com.sertech.todolist.repository;

import br.com.sertech.todolist.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUsername(String userName);
}
