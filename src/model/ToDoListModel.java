package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.ToDoList;
import entity.User;
import helper.SecurityHelper;
import helper.ServletUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ToDoListModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "to_do_list";
    final static Logger logger = Logger.getLogger(ToDoListModel.class);
    public static ToDoListModel INSTANCE = new ToDoListModel();

    private ToDoListModel() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public List<ToDoList> getSliceToDoList(int idUser) {
        List<ToDoList> resultToDoList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultToDoList;

            }
            PreparedStatement getAllToDoListByIdUser = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id_user = ?");
            getAllToDoListByIdUser.setInt(1, idUser);
            ResultSet rs = getAllToDoListByIdUser.executeQuery();

            while (rs.next()) {
                ToDoList toDoList = new ToDoList();
                toDoList.setId(rs.getInt("id"));
                toDoList.setIdUser(rs.getInt("id_user"));
                toDoList.setToDoList(rs.getString("to_do"));

                resultToDoList.add(toDoList);
            }

            return resultToDoList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultToDoList;
    }


    public ToDoList getToDoListByID(int id) {
        ToDoList result = new ToDoList();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getToDoListByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE id = ? ");
            getToDoListByIdStmt.setInt(1, id);

            ResultSet rs = getToDoListByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setIdUser(rs.getInt("id_user"));
                result.setToDoList(rs.getString("to_do"));
            }

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public int addToDoList(int idUser, String toDoList) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }

            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (id_user, to_do) "
                    + "VALUES (?, ?)");
            addStmt.setInt(1, idUser);
            addStmt.setString(2, toDoList);
            int rs = addStmt.executeUpdate();
            return rs;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return ErrorCode.FAIL.getValue();
    }

    public int deleteToDoList(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            ToDoList toDoListByID = getToDoListByID(id);
            if (toDoListByID.getId() == 0) {
                return ErrorCode.NOT_EXIST.getValue();
            }
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM `" + NAMETABLE + "` WHERE id = ?");
            deleteStmt.setInt(1, id);
            int rs = deleteStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

}
