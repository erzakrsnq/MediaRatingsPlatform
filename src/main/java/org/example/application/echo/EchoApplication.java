package org.example.application.echo;

import org.example.application.common.Application;
import org.example.server.http.Request;
import org.example.server.http.Response;

public class EchoApplication implements Application {

    @Override
    public Response handle(Request request) {
        Response response = new Response();


        return response;
    }
}