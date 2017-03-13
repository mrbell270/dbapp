package delta.api;

import delta.database.UserController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

/**
 * Created by lenovo on 16.02.2017.
 */

@RestController
public class UserAPI {
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ModelAndView addUser(@RequestParam String userName,
                                @RequestParam String password,
                                @RequestParam(required = false) String description) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            new UserController().createUser(userName, password, description);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on creating user");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        modelAndView.setViewName("redirect:" + "/login");
        modelAndView.setStatus(HttpStatus.CREATED);
        return modelAndView;
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ModelAndView auth(@RequestParam String userName,
                    @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            String token = new UserController().auth(userName, password);
            modelAndView.setViewName("redirect:" + "/home");
            modelAndView.addObject("token", token);
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.addObject("err", "Wrong password");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.setViewName("redirect:" + "/autherror");
            return modelAndView;
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.addObject("err", "No such user");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.setViewName("redirect:" + "/autherror");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public Response deleteUser(@RequestParam String token,
                               @RequestParam String userName,
                               @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            new UserController().deleteUser(token, userName, password);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Auth needed").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Error on deleting user").build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @RequestMapping(value = "/user/pass", method = RequestMethod.PUT)
    public Response updateUserPass(@RequestParam String token,
                                   @RequestParam String userName,
                                   @RequestParam String password) {
        try {
            new UserController().updateUserPass(token, userName, password);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Auth needed").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Error on updating user pass").build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @RequestMapping(value = "/user/desc", method = RequestMethod.PUT)
    public Response updateUserDesc(@RequestParam String token,
                                   @RequestParam String userName,
                                   @RequestParam String desc) {
        try {
            new UserController().updateUserDesc(token, userName, desc);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Auth needed").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Error on updating user desc").build();
        }
        return Response.status(Response.Status.CREATED).build();
    }
}
