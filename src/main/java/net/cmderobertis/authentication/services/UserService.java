package net.cmderobertis.authentication.services;
import net.cmderobertis.authentication.models.LoginUser;
import org.mindrot.jbcrypt.BCrypt;
import net.cmderobertis.authentication.models.User;
import net.cmderobertis.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository repo;

    public User register(User user, BindingResult result) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "Taken", "Email is already in use");
        }
        if (!user.getPassword().equals(user.getConfirm())) {
            result.rejectValue("confirm", "Matches", "Passwords must match");
        }
        if (result.hasErrors()) {
            return null;
        }
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        repo.save(user);
        return user;
    }
    public User login(LoginUser newLoginObject, BindingResult result) {
        User userInDB = repo.findByEmail(newLoginObject.getEmail()).get();
        if (!repo.findByEmail(newLoginObject.getEmail()).isPresent()) {
            result.rejectValue("email", "Unknown", "Invalid Email");
        }
        if(!BCrypt.checkpw(newLoginObject.getPassword(), userInDB.getPassword())) {
            result.rejectValue("password", "Matches", "Invalid Password");
        }
        if (result.hasErrors()) {
            return null;
        }
        return userInDB;
    }
    // CRUD Methods
    public List<User> getAll() {
        return repo.findAll();
    }
    public User getOne(Long id) {
        Optional<User> user = repo.findById(id);
        return user.orElse(null);
    }
    public void update(User user) { repo.save(user); }
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
