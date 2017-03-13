package delta.api;

import delta.database.GoodsController;
import delta.entity.Goods;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.List;

/**
 * Created by lenovo on 23.02.2017.
 */

@RestController
public class GoodsAPI {

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public ModelAndView addGoods(@RequestParam String token,
                                 @RequestParam String name,
                                 @RequestParam int cost,
                                 @RequestParam(required = false) String description) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("token", token);
        try {
            new GoodsController().addGoods(token, name, cost, description);
            modelAndView.setViewName("redirect:" + "/goods");
            modelAndView.setStatus(HttpStatus.CREATED);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on adding goods");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/goods/{id}", method = RequestMethod.GET)
    public ModelAndView getGoodsByID(@PathVariable("id") String id, @RequestParam String token){
        try {
            Goods result = new GoodsController().getGoodsByID(token, new Integer(id));
            //return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            //return Response.status(Response.Status.BAD_REQUEST).entity("Auth needed").build();
        } catch (SQLException e) {
            e.printStackTrace();
            //return Response.status(Response.Status.BAD_REQUEST).entity("Error on getting goods by id").build();
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setStatus(HttpStatus.CREATED);
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }

    @RequestMapping(value = "/goods", method = RequestMethod.GET)
    public ModelAndView getAllGoods(@RequestParam String token){
        ModelAndView modelAndView = new ModelAndView("/allGoods");
        modelAndView.addObject("token", token);
        try {
            List<Goods> goods = new GoodsController().getGoods(token);
            modelAndView.addObject("goods", goods);
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on getting goods");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/goods", method = RequestMethod.DELETE)
    public ModelAndView deleteGoods(@RequestParam String token,
                                    @RequestParam int id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("token", token);
        try {
            new GoodsController().deleteGoods(token, id);
            modelAndView.setViewName("redirect:" + "/goods");
            modelAndView.setStatus(HttpStatus.CREATED);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on deleting goods");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/goods", method = RequestMethod.PUT)
    public Response updateGoods(@RequestParam String token,
                                @RequestParam String name,
                                @RequestParam int cost) {
        try {
            new GoodsController().updateGoods(token, name, cost);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Auth needed").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Error on updating goods").build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
