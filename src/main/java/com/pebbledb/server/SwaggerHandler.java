package com.pebbledb.server;

import com.jsoniter.output.JsonStream;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.info.Contact;
import io.swagger.oas.models.info.Info;
import io.swagger.oas.models.info.License;
import io.swagger.oas.models.tags.Tag;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class SwaggerHandler implements HttpHandler {

    Info info = new Info()
            .title("Swagger Petstore")
            .description("This is a sample server Petstore server.  You can find out more about Swagger " +
                    "at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).  For this sample, " +
                    "you can use the api key `special-key` to test the authorization filters.")
            .termsOfService("http://swagger.io/terms/")
            .contact(new Contact()
                    .email("maxdemarzi@hotmail.com"))
            .license(new License()
                    .name("GNU General Public License v3.0")
                    .url("https://www.gnu.org/licenses/gpl-3.0.en.html"));

    OpenAPI swagger = new OpenAPI().info(info);


    public void handleRequest(HttpServerExchange exchange) throws Exception {
        swagger.addTagsItem(new Tag().name("pet").description("Everything about your Pets")
                .externalDocs(new io.swagger.oas.models.ExternalDocumentation().description("Find out more").url("http://swagger.io")));

        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(JsonStream.serialize(Types.OBJECT, swagger));
    }
}
