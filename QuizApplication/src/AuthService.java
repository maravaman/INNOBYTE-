import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt

class AuthService {
    private UserDao userDao; // Assuming you'll have a UserDao

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        User user = userDao.getUserByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            return user; // Login successful
        }
        return null; // Login failed
    }

    public boolean register(String username, String password) {
        if (userDao.getUserByUsername(username) != null) {
            return false; // Username already exists
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // Hash the password
        User newUser = new User(0, username, hashedPassword); // ID will be set by DAO
        return userDao.addUser(newUser);
    }
}
