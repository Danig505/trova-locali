package its.compito.locali.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import its.compito.locali.persistence.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Integer> {

    public User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }

    public List<User> getFilteredUsers(String search, String roleFilter) {
        String query = "1=1";
        Map<String, Object> params = new HashMap<>();

        if (search != null && !search.trim().isEmpty()) {
            query += " and lower(username) like :search";
            params.put("search", "%" + search.toLowerCase() + "%");
        }
        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            query += " and role = :role";
            params.put("role", roleFilter);
        }

        return find(query, params).list();
    }

    public void updateUserRole(int id, String newRole) {
        update("role = ?1 where id = ?2", newRole, id);
    }
}