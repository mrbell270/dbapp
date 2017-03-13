package delta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by lenovo on 06.03.2017.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/createUser").setViewName("createUser");
        registry.addViewController("/Orders").setViewName("Orders");
        registry.addViewController("/Billings").setViewName("Billings");
        registry.addViewController("/Goods").setViewName("Goods");
        registry.addViewController("/allGoods").setViewName("allGoods");
        registry.addViewController("/dbsuccess").setViewName("dbsuccess");
        registry.addViewController("/dberror").setViewName("dberror");
        registry.addViewController("/autherror").setViewName("autherror");
    }

}