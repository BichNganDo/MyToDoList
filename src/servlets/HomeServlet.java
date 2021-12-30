package servlets;

import com.google.gson.Gson;
import common.APIResult;
import common.Config;
import entity.ToDoList;
import entity.User;
import helper.HttpHelper;
import helper.ServletUtil;
import helper.SessionHelper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import model.ToDoListModel;
import model.UserModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import templater.PageGenerator;

public class HomeServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("app_domain", Config.APP_DOMAIN);
        pageVariables.put("static_domain", Config.STATIC_DOMAIN);

        int idUser = JWTModel.INSTANCE.getIdUser(request);
        List<ToDoList> sliceToDoList = ToDoListModel.INSTANCE.getSliceToDoList(idUser);
        pageVariables.put("all_to_do_list", sliceToDoList);

        User userByID = UserModel.INSTANCE.getUserByID(idUser);
        String avatar = userByID.getAvatar();
        String name = userByID.getName();
        pageVariables.put("avatar", avatar);
        pageVariables.put("name", name);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(PageGenerator.instance().getPage("home.html", pageVariables));

        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "add": {
                String toDoList = request.getParameter("toDoList");
                int idUser = JWTModel.INSTANCE.getIdUser(request);

                int addToDoList = ToDoListModel.INSTANCE.addToDoList(idUser, toDoList);

                if (addToDoList >= 0) {
                    response.sendRedirect(Config.APP_DOMAIN + "/");

                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Thêm thất bại!");
                }
                break;
            }

            case "delete": {
                String body = HttpHelper.getBodyData(request);
                JSONObject jbody = new JSONObject(body);

                int id = NumberUtils.toInt(jbody.optString("id"));
                int deleteToDoList = ToDoListModel.INSTANCE.deleteToDoList(id);
                if (deleteToDoList >= 0) {
                    result.setErrorCode(0);
                } else {
                    result.setErrorCode(-2);
                    result.setMessage("Xóa thất bại!");
                }
                break;
            }
            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}
