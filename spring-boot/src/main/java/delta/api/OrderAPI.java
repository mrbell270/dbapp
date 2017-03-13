package delta.api;

import delta.database.OrderController;
import delta.database.UserController;
import delta.entity.Billing;
import delta.entity.Goods;
import delta.entity.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.AssertFalse;
import javax.ws.rs.core.Response;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 26.02.2017.
 */
@RestController
public class OrderAPI {
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public ModelAndView addOrder(@RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("token", token);
        try {
            new OrderController().addOrder(token);
            modelAndView.setViewName("redirect:" + "/orders");
            modelAndView.setStatus(HttpStatus.CREATED);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on adding order");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/orders/{orderid}/goods", method = RequestMethod.POST)
    public ModelAndView addGoodsToOrder(@PathVariable("orderid") String orderid,
                                    @RequestParam int goodsid,
                                    @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("/Goods");
        modelAndView.addObject("token", token);
        try {
            new OrderController().addGoodsToOrder(token, new Integer(orderid), goodsid);
            List<Goods> goods = new OrderController().getGoodsInOrder(token, new Integer(orderid));
            modelAndView.addObject("goods", goods);
            modelAndView.setStatus(HttpStatus.CREATED);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLClientInfoException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Wrong orderID");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on adding goods to order");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/orders/{orderid}/billing", method = RequestMethod.POST)
    public ModelAndView getBilling(@PathVariable("orderid") String orderid, @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("token", token);
        try {
            new OrderController().addBilling(token, new Integer(orderid));
            modelAndView.setViewName("redirect:" + "/billings");
            modelAndView.setStatus(HttpStatus.CREATED);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLClientInfoException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Wrong orderID");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on creating bill");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView getOrders(@RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("/Orders");
        modelAndView.addObject("token", token);
        try {
            List<Order> orders = new OrderController().getOrders(token);
            modelAndView.addObject("orders", orders);
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on getting orders");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/billings", method = RequestMethod.GET)
    public ModelAndView getBillings(@RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("/Billings");
        modelAndView.addObject("token", token);
        try {
            List<Billing> billings = new OrderController().getBillings(token);
            modelAndView.addObject("billings", billings);
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on getting billings");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/orders/{orderid}", method = RequestMethod.GET)
    public ModelAndView getGoodsInOrder(@PathVariable("orderid") String orderid, @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("/Goods");
        modelAndView.addObject("token", token);
        try {
            List<Goods> goods = new OrderController().getGoodsInOrder(token, new Integer(orderid));
            modelAndView.addObject("goods", goods);
            modelAndView.addObject("orderid", orderid);
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
        } catch (SQLClientInfoException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Wrong orderID");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on getting order info");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.DELETE)
    public ModelAndView deleteOrder(@RequestParam String token, @RequestParam int orderid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("token", token);
        try {
            new OrderController().deleteOrder(token, orderid);
            modelAndView.setViewName("redirect:" + "/orders");
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on deleting order");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/orders/{orderid}/goods", method = RequestMethod.DELETE)
    public ModelAndView deleteGoodsFromOrder(@PathVariable("orderid") String orderid,
                                         @RequestParam int goodsid,
                                         @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("/Goods");
        modelAndView.addObject("token", token);
        try {
            new OrderController().deleteGoodsFromOrder(token, new Integer(orderid), goodsid);
            List<Goods> goods = new OrderController().getGoodsInOrder(token, new Integer(orderid));
            modelAndView.addObject("goods", goods);
            modelAndView.setStatus(HttpStatus.OK);
        } catch (SQLInvalidAuthorizationSpecException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/autherror");
            modelAndView.addObject("err", "Auth needed");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLClientInfoException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Wrong orderID");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            modelAndView.setViewName("redirect:" + "/dberror");
            modelAndView.addObject("err", "Error on adding goods to order");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }
}
