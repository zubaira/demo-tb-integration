package com.example.demo;


import com.example.demo.processors.InjectHeadersProcessor;
import com.example.demo.routes.BasicDhis2ApiRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DemoApplication {

    @Autowired
    private BasicDhis2ApiRoute route;

    public static void main(String[] args) throws Exception
    {
        SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Bean
    public CamelContext createCamelContext() throws Exception
    {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes( route );
        context.start();

        ProducerTemplate template = context.createProducerTemplate();
        template.sendBodyAndHeader("direct:start", null, "me", "Donald Duck");

        context.stop();
        return context;
    }
}
