package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.User;
import helper.SecurityHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class UserModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "user";
    final static Logger logger = Logger.getLogger(UserModel.class);
    public static UserModel INSTANCE = new UserModel();

    private UserModel() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public User getUserByID(int id) {

        User result = new User();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getUserRegisterByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getUserRegisterByIdStmt.setInt(1, id);

            ResultSet rs = getUserRegisterByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setName(rs.getString("name"));
                result.setEmail(rs.getString("email"));
                result.setPassword(rs.getString("password"));
                result.setSalt(rs.getString("salt"));
                result.setAvatar(rs.getString("avatar"));
            }

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public User getUserByEmail(String email) {

        User result = new User();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getUserRegisterByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE email = ? ");
            getUserRegisterByIdStmt.setString(1, email);

            ResultSet rs = getUserRegisterByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setName(rs.getString("name"));
                result.setEmail(rs.getString("email"));
                result.setPassword(rs.getString("password"));
                result.setSalt(rs.getString("salt"));
                result.setAvatar(rs.getString("avatar"));

            }

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public boolean isExistEmail(String email) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return false;
            }

            PreparedStatement isExistEmailStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE `email` = ?");
            isExistEmailStmt.setString(1, email);

            ResultSet rs = isExistEmailStmt.executeQuery();
            if (rs.next()) {
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return false;
    }

    public int addUser(String name, String email, String password, String avatar) {
        Connection conn = null;
        Boolean isExistEmail = INSTANCE.isExistEmail(email);
        if (isExistEmail == true) {
            return ErrorCode.EXIST_ACCOUNT.getValue();
        }
        String salt = SecurityHelper.genRandomSalt();
        password = SecurityHelper.getMD5Hash(password + salt);
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (name, email, password, salt, avatar) "
                    + "VALUES (?, ?, ?, ?, ?)");
            addStmt.setString(1, name);
            addStmt.setString(2, email);
            addStmt.setString(3, password);
            addStmt.setString(4, salt);
            addStmt.setString(5, avatar);
            int rs = addStmt.executeUpdate();
            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return ErrorCode.FAIL.getValue();
    }

    public User checkLogin(String email, String password) {
        User user = new User();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return user;
            }
            PreparedStatement checkLoginStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE email = ? AND password = ?");
            checkLoginStmt.setString(1, email);
            checkLoginStmt.setString(2, password);
            ResultSet rs = checkLoginStmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAvatar(rs.getString("avatar"));
            }

            return user;

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return user;
    }
}
