package com.example.demo.processors;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author rajazubair
 */

@Component
public class ObjectMapperProcessor implements Processor
{
    @Override
    public void process(Exchange exchange) throws Exception {

        Boolean result = exchange.getIn().getBody(Boolean.class);

        result = !result;

        exchange.getIn().setBody( result );
    }
}
