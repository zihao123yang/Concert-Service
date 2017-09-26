package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.NewsItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zihaoyang on 25/09/17.
 */
public class SubscriptionResource {

    protected Map<String, AsyncResponse> _subscribeList = new HashMap<>( );

    @GET
    @Path("/subscribe")
    public void subscribe(@Suspended AsyncResponse response, @CookieParam("subscriptionCheck") Cookie cookie) {

        EntityManager entityManager = PersistenceManager.instance().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        // the cookie contains the authentication token for the user and the timestamp of the last NewsItem the user received
        // split them from the cookie into two separate Strings
        String[] cookieValues = cookie.getValue().split("/");

        //if there is a authentication token and a timestamp in the cookie then the user is resubscribing
        if (cookieValues.length == 2) {
            //get all the newsItems that were added to the database after the last newsItem the user got
            //the listed returned by the query is ordered by their timestamps
            TypedQuery<NewsItem> query = entityManager.createQuery("select n from NewsItem n where n._timestamp > :timestamp order by n._timestamp", NewsItem.class)
                    .setParameter("timestamp", cookieValues[1]);
            List<NewsItem> newsItems = query.getResultList();
            //convert the list of NewsItems to a list of NewsItemDTOs
            List<NewsItemDTO> newsItemDTOs = Mapper.newsItemTONewsItemDTO(newsItems);

            if (!newsItems.isEmpty()) {
                response.resume(newsItemDTOs);
            }
            _subscribeList.put(cookieValues[0], response);
        } else if (cookieValues.length == 1) {
            _subscribeList.put(cookieValues[0], response);
        }
    }

    @POST
    @Path("/updateNews")
    @Consumes({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public void send(NewsItemDTO newsItemDTO) {

        EntityManager entityManager = PersistenceManager.instance().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        NewsItem newsItem = Mapper.newsItemDTOToNewsItem(newsItemDTO);
        entityManager.persist(newsItem);

        for (Map.Entry<String, AsyncResponse> entry : _subscribeList.entrySet()) {
            entry.getValue().resume(newsItem);
        }
        _subscribeList.clear( );
    }


    @DELETE
    @Path("/unSubscribe")
    public void unSubscribe(@CookieParam("subscriptionCheck") Cookie cookie) {

        String[] cookieValues = cookie.getValue().split("/");
        _subscribeList.remove(cookieValues[0]);

    }












}
