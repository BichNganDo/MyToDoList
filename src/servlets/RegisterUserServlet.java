package servlets;

import com.google.gson.Gson;
import common.APIResult;
import common.Config;
import entity.User;
import helper.SecurityHelper;
import helper.ServletUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import model.UserModel;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import templater.PageGenerator;

public class RegisterUserServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(RegisterUserServlet.class);

    public RegisterUserServlet() {
        PropertyConfigurator.configure("log4j.properties");

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("app_domain", Config.APP_DOMAIN);
        pageVariables.put("static_domain", Config.STATIC_DOMAIN);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(PageGenerator.instance().getPage("register.html", pageVariables));

        response.setStatus(HttpServletResponse.SC_OK);

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {
                User user = new User();
                ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                upload.setHeaderEncoding("UTF-8");

                List<FileItem> items = upload.parseRequest(request);
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                        String fieldname = item.getFieldName();
                        String fieldvalue = item.getString("UTF-8");

                        switch (fieldname) {
                            case "name": {
                                user.setName(fieldvalue);
                                break;
                            }
                            case "email": {
                                user.setEmail(fieldvalue);
                                break;
                            }
                            case "password": {
                                user.setPassword(fieldvalue);
                                break;
                            }

                        }

                    } else {
                        // Process form file field (input type="file").
                        String filename = FilenameUtils.getName(item.getName());
                        if (StringUtils.isNotEmpty(filename)) {
                            InputStream a = item.getInputStream();
                            Path uploadDir = Paths.get("avatar/" + filename);
                            Files.copy(a, uploadDir, StandardCopyOption.REPLACE_EXISTING);
                            user.setAvatar("avatar/" + filename);
                        } else {
                            user.setAvatar("avatar/unnamed.png");
                        }


                    }
                }
                int addUserRegister = UserModel.INSTANCE.addUser(user.getName(),
                        user.getEmail(), user.getPassword(), user.getAvatar());

                if (addUserRegister >= 0) {
                    response.sendRedirect(Config.APP_DOMAIN + "/login");
                } else {
                    result.setErrorCode(-1);
                    result.setMessage("Đăng ký thất bại!");
                }
            } else {
                result.setErrorCode(-4);
                result.setMessage("Có lỗi");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        ServletUtil.printJson(request, response, gson.toJson(result));
    }


}
