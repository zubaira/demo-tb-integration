package com.example.demo.routes;

/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.example.demo.processors.InjectHeadersProcessor;
import com.example.demo.processors.ObjectMapperProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author Zubair Asghar
 */

@Component
public class BasicDhis2ApiRoute extends RouteBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger( BasicDhis2ApiRoute.class );

    @Value( "${dhis2.api.host}")
    private String host;

    @Value("${dhis2.api.port}")
    private int port;

    @Value("${dhis2.api.setting}")
    private String settingUrl;

    @Autowired
    private InjectHeadersProcessor injectAuthenticationHeaderProcessor;

    @Autowired
    private ObjectMapperProcessor objectMapperProcessor;

    @Override
    public void configure()
    {
        onException( HttpOperationFailedException.class )
                .log( LoggingLevel.ERROR,
                        "HTTP response body => ${exchangeProperty.CamelExceptionCaught.responseBody}" )
                .process( exchange -> {
                    throw (Exception) exchange.getProperty("CamelExceptionCaught" );
                } );

        restConfiguration().component("jetty")
                .host(host)
                .port(port)
                .bindingMode(RestBindingMode.json);

        from("direct:start")
                .process(injectAuthenticationHeaderProcessor)
                .toD("rest:get:" + settingUrl)
                .log(LoggingLevel.INFO, "http response code: ${header.CamelHttpResponseCode}")
                .log(LoggingLevel.INFO, "Payload data: ${body}")

                .to("file:/Users/rajazubair/camel-test-to?fileName=response.txt")
                .log(LoggingLevel.INFO, " Response written in file ")
                .to("direct:changeSetting");

        from("direct:changeSetting" )
                .process( objectMapperProcessor )
                .to("rest:post:/dhis_war/api/39/userSettings/keyMessageSmsNotification")
                .log(LoggingLevel.INFO, "http response code: ${header.CamelHttpResponseCode}")
                .log( "User Settings updated" );


    }
}
