package nz.ac.auckland.concert.service.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zihaoyang on 25/09/17.
 */
public class SubscriptionResource {

    protected List<AsyncResponse> responses = new ArrayList<AsyncResponse>( );

    @GET
    @Path("/subscribe")
    public void subscribe(@Suspended AsyncResponse response ) {
        responses.add( response );
    }

    @POST
    @Consumes({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public void send( String message ) {

        for (AsyncResponse response : responses) {
            response.resume( message );
        }
        responses.clear( ); }










}
