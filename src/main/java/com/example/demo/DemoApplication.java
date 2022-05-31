package com.example.demo;


import com.example.demo.routes.BasicDhis2ApiRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DemoApplication {


    public static void main(String[] args) throws Exception
    {
        SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        CamelContext context = new DefaultCamelContext();
        context.addRoutes( new BasicDhis2ApiRoute() );

        context.start();

        ProducerTemplate template = context.createProducerTemplate();
        template.sendBodyAndHeader("direct:start", null, "me", "Donald Duck");
        Thread.sleep(5000);
    }
}
