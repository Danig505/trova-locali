package its.compito.locali.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import its.compito.locali.persistence.entity.User;
import its.compito.locali.persistence.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public RegistrationResult register(String username, String password, String confirmPassword) {

        if (username == null || username.isBlank()) {
            return RegistrationResult.error("Username is required.");
        }
        if (password == null || password.length() < 6) {
            return RegistrationResult.error("Password must be at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            return RegistrationResult.error("Passwords do not match.");
        }

        String trimmed = username.strip();

        if (userRepository.existsByUsername(trimmed)) {
            return RegistrationResult.error("Username already taken.");
        }

        String hashed = BcryptUtil.bcryptHash(password);

        User u = new User();
        u.setUsername(trimmed);
        u.setRole("USER");
        u.setPassword(hashed);

        userRepository.persist(u);
        return RegistrationResult.success();
    }

    @Transactional
    public Optional<String> modifyUser(String oldUsername, String newUsername, String newRole) {
        Optional<User> oldByUsername = Optional.ofNullable(userRepository.findByUsername(oldUsername));

        if (oldByUsername.isEmpty()) {
            return Optional.of("User does not exist");
        }

        if (!oldUsername.equals(newUsername)) {
            if (userRepository.existsByUsername(newUsername)) {
                return Optional.of("Username already exists");
            }
        }

        User user = oldByUsername.get();
        user.setUsername(newUsername);
        user.setRole(newRole);

        return Optional.empty();
    }

    public record RegistrationResult(boolean ok, String errorMessage) {
        public static RegistrationResult success() {
            return new RegistrationResult(true, "");
        }
        public static RegistrationResult error(String message) {
            return new RegistrationResult(false, message);
        }
    }
}