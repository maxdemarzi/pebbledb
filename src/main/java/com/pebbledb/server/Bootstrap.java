package com.pebbledb.server;

import com.jsoniter.output.JsonStream;
import io.swagger.oas.models.ExternalDocumentation;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.info.Contact;
import io.swagger.oas.models.info.Info;
import io.swagger.oas.models.info.License;
import io.swagger.oas.models.tags.Tag;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Bootstrap extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        Info info = new Info()
                .title("Swagger Sample App")
                .description("This is a sample server Petstore server.  You can find out more about Swagger " +
                        "at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).  For this sample, " +
                        "you can use the api key `special-key` to test the authorization filters.")
                .termsOfService("http://helloreverb.com/terms/")
                .contact(new Contact()
                        .email("maxdemarzi@hotmail.com"))
                .license(new License()
                        .name("GNU General Public License v3.0")
                        .url("https://www.gnu.org/licenses/gpl-3.0.en.html"));

        ServletContext context = config.getServletContext();
        OpenAPI swagger = new OpenAPI().info(info);
        swagger.externalDocs(new ExternalDocumentation()
                .description("Find out more about Swagger").url("http://swagger.io"));
//        swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", SecurityScheme.In.HEADER));
//        swagger.securityDefinition("petstore_auth",
//                new OAuth2Definition()
//                        .implicit("http://petstore.swagger.io/api/oauth/dialog")
//                        .scope("read:pets", "read your pets")
//                        .scope("write:pets", "modify pets in your account"));
        swagger.addTagsItem(new Tag()
                .name("pet")
                .description("Everything about your Pets")
                .externalDocs(new ExternalDocumentation().description("Find out more").url("http://swagger.io")));
        swagger.addTagsItem(new Tag()
                .name("store")
                .description("Access to Petstore orders"));
        swagger.addTagsItem(new Tag()
                .name("user")
                .description("Operations about user")
                .externalDocs(new ExternalDocumentation().description("Find out more about our store").url("http://swagger.io")));

        /*
        swagger.addTagsItem(new Tag().name("pet").description("Everything about your Pets")
                .externalDocs(new io.swagger.oas.models.ExternalDocumentation().description("Find out more").url("http://swagger.io")));

         */

        context.setAttribute("swagger", swagger);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OpenAPI swagger = (OpenAPI) request.getSession().getServletContext().getAttribute("swagger");
        JsonStream.serialize(Types.OBJECT, swagger);
        response.getOutputStream().write(JsonStream.serialize(Types.OBJECT, swagger).getBytes());
    }
}