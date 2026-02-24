package lk.ijse.etecmanagementsystem.dao.custom;

import lk.ijse.etecmanagementsystem.dto.UserDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    List<UserDTO> getAllUsers() throws SQLException;

    UserDTO getUserById(int id) throws SQLException;

    boolean saveUser(UserDTO user) throws SQLException;

    boolean updateUser(UserDTO user) throws SQLException;

    boolean deleteUser(int id) throws SQLException;

    boolean validateCredentials(String username, String password) throws SQLException;

    boolean validateUserName(String username) throws SQLException;

    String getUserRole(String username) throws SQLException;

    String getName(String username) throws SQLException;

    int getUserId(String username) throws SQLException;

    boolean validateUserEmail(String username, String email) throws SQLException;

    String getUserPassword(String username) throws SQLException;
}

