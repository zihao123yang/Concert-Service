package nz.ac.auckland.concert.service.services;

import com.sun.org.apache.regexp.internal.RE;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.*;
import nz.ac.auckland.concert.service.util.TheatreUtility;
import org.apache.http.auth.AUTH;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static javax.ws.rs.core.Response.status;

/**
 * Created by zihaoyang on 19/09/17.
 */
@Path("/concerts")
public class ConcertResource {

    EntityManager entityManager = PersistenceManager.instance().createEntityManager();

    @GET
    @Path("/all")
    @Produces({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public Response retrieveConcerts() {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //load all concerts persisted in the Database
        TypedQuery<Concert> query = entityManager.createQuery("select c from Concert c", Concert.class);
        List<Concert> concerts = query.getResultList();

        //convert list of Concert to a Set of ConcertDTOs
        Set<ConcertDTO> concertDTOs = Mapper.concertToConcertDTO(concerts);

        Response.ResponseBuilder builder;
        //check whether there are any Concerts loaded
        if (concerts == null) {
            builder = status(404);
        } else {
            //create a GenericEntity for ConcertDTO so that the Set of ConcertDTOs can be sent over to client side
            GenericEntity<Set<ConcertDTO>> entity = new GenericEntity<Set<ConcertDTO>> (concertDTOs) { };
            builder = Response.ok(entity);
        }
        entityManager.close();
        return builder.build();
    }

    @GET
    @Path("/allPerformers")
    @Produces({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public Response retrievePerformers() {

        EntityManager entityManager = PersistenceManager.instance().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //load all the Performers from the database
        TypedQuery<Performer> query = entityManager.createQuery("select p from Performer p", Performer.class);
        List<Performer> performers = query.getResultList();

        //convert Performers from database to PerformerDTOs so it can be sent over to client side
        Set<PerformerDTO> performerDTOs = Mapper.performerToConcertDTO(performers);

        Response.ResponseBuilder builder;
        if (performers == null) {
            builder = status(404);
        } else {
            //create GenericEntity for PerformerDTO
            GenericEntity<Set<PerformerDTO>> entity = new GenericEntity<Set<PerformerDTO>> (performerDTOs) { };
            builder = Response.ok(entity);
        }
        entityManager.close();
        return builder.build();

    }

    @POST
    @Path("/user")
    @Consumes({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public Response createUser(UserDTO newUser) {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //checking to see if the UserDTO has all the required attributes, if not throw an Exception
        if (newUser.getLastname() == null || newUser.getFirstname() == null || newUser.getUsername() == null || newUser.getPassword() == null) {
            throw new BadRequestException(Response
            .status(Response.Status.BAD_REQUEST)
            .entity(Messages.CREATE_USER_WITH_MISSING_FIELDS)
            .build());

        }

        //check to see if the username specified by the new User is already in the database
        User existingUser = entityManager.find(User.class, newUser.getUsername());
        if (existingUser != null) {
            throw new BadRequestException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)
                    .build());
        }

        User user = Mapper.userDTOToUser(newUser);
        //create new cookie containing the authentication token for the particular User
        NewCookie cookie = makeCookie();

        //create an Authentication object with the token inside
        AuthenticationDetail token = new AuthenticationDetail(cookie.getValue(), user);
        //persist user to database
        entityManager.persist(user);
        //persist Authentication details to database
        entityManager.persist(token);
        transaction.commit();

        Response.ResponseBuilder builder;
        builder = Response.status(201);
        builder.entity(newUser);
        builder.cookie(cookie);

        entityManager.close();
        return builder.build();
    }


    @PUT
    @Path("/authenticate")
    @Consumes({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public Response authenticateUser(UserDTO userDTO) {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //check if the UserDTO contains both a username and a password
        if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS)
                    .build());
        }

        //throw an Exception if trying to authenticate a non existing User- ie User not in database table
        User existingUser = entityManager.find(User.class, userDTO.getUsername());
        if (existingUser == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.AUTHENTICATE_NON_EXISTENT_USER)
                    .build());
        }

        //throw an Exception if the password given by UserDTO does not match the one stored by the service
        if (!userDTO.getPassword().equals(existingUser.getPassword())) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD)
                    .build());
        }

        //throw an Exception if the UserDTO passed over does not contain a first name or last name
        if (userDTO.getFirstname() == null || userDTO.getLastname() == null) {
            userDTO = new UserDTO(existingUser.getUserName(),existingUser.getPassword(), existingUser.getLastName(), existingUser.getFirstName());
        }

//        System.out.println("username: " + existingUser.getUserName());
//        System.out.println("password: " + existingUser.getPassword());
//        System.out.println("first name: " + existingUser.getFirstName());
//        System.out.println("last name: " + existingUser.getLastName());


        //create cookie with an authentication token for the User
        NewCookie cookie = makeCookie();
        //create a new Authentication object with the token as its attribute and persist to database
        AuthenticationDetail token = new AuthenticationDetail(cookie.getValue(), existingUser);
        entityManager.persist(token);
        transaction.commit();

        Response.ResponseBuilder builder;
        builder = Response.status(200);
        builder.entity(userDTO);
        builder.cookie(cookie);

        entityManager.close();
        return builder.build();

    }

    @POST
    @Path("/reserve")
    @Consumes({javax.ws.rs.core.MediaType.APPLICATION_XML})
    @Produces({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public Response reserveSeats(ReservationRequestDTO reservationRequest, @CookieParam("userToken") Cookie cookie) {

        //timestamp is an indication of the moment this method is called, it will be used to check if reservation has expired
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        System.out.println("cookie value: " + cookie.getValue());
        //if user is unauthenticated/no Cookie passed from client side, then throw Exception
        if (cookie.getValue().equals("")) {
            System.out.println("hello");
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.UNAUTHENTICATED_REQUEST)
                    .build());
        }

        //attempts to obtain authentication detail from database
        AuthenticationDetail authentication = loadAuthenticationDetail(cookie.getValue());

        // if the token provided by client side is not matched by one in the database, throw an Exception
        if (authentication == null) {
            System.out.println("wrong one bro");
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.BAD_AUTHENTICATON_TOKEN)
                    .build());
        }

        // if not all the required attributes of the ReservationRequest is filled out, throw an Exception
        if (reservationRequest.getConcertId() == null || reservationRequest.getNumberOfSeats() == 0 || reservationRequest.getSeatType() == null || reservationRequest.getDate() == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS)
                    .build());
        }
        //load the Concert for which the reservation request was made from the database
        Concert concert = entityManager.find(Concert.class, reservationRequest.getConcertId());
        Set<LocalDateTime> concertDates = concert.getLocalDateTimes();

        //check if the date for the reservation request is a valid date and the concert is actually held on that date
        if(!concertDates.contains(reservationRequest.getDate())) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
                    .build());
        }

        //load all the seats that have been booked on the date specified by the reservation request
        //selecting by the date and time because there shouldn't be multiple concerts on at the same time
        TypedQuery<Seat> query = entityManager.createQuery("select s from Seat s where s._date = :date", Seat.class)
                .setParameter("date", reservationRequest.getDate());
        List<Seat> seatsList = query.getResultList();
        //convert the List of Seats that have already been booked or reserved to a Set of SeatDTOs using method from the Mapper class
        Set<Seat> seats = new HashSet<>(seatsList);
        Set<SeatDTO> seatDTOs = Mapper.seatsToSeatDTOs(seats);
        //obtain a set of seats that the user could reserve if there are enough free seats for the particular concert and price band
        Set<SeatDTO> availableSeats = TheatreUtility.findAvailableSeats(reservationRequest.getNumberOfSeats(), reservationRequest.getSeatType(),seatDTOs);
        //if there are not enough free seats for the User to reserve, throw an Exception
        if (availableSeats.isEmpty()) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST).entity(Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION)
                    .build());
        }

        //convert the Set of SeatDTOs to be reserved to a Set of Seats
        Set<Seat> seatsToReserve = Mapper.seatDTOsToSeats(availableSeats, reservationRequest.getDate());
        //create the reservation on the free seats obtained previously
        Reservation reservation = new Reservation(reservationRequest.getNumberOfSeats(), reservationRequest.getSeatType(),reservationRequest.getDate(), seatsToReserve, concert, authentication.getUser(), timestamp.getTime());

        entityManager.persist(reservation);

        //create a ReservationDTO to send back to the client side
        ReservationDTO reservationDTO = new ReservationDTO(reservation.getId(), reservationRequest, availableSeats);

        Response.ResponseBuilder builder;
        builder = Response.status(201);
        builder.entity(reservationDTO);
        System.out.println(reservationDTO.getId() + " " + reservationDTO.getSeats().size());

        transaction.commit();
        entityManager.close();
        return builder.build();

    }


    @POST
    @Path("/confirm")
    @Consumes({javax.ws.rs.core.MediaType.APPLICATION_XML})
    public Response confirmReservation(ReservationDTO reservationDTO, @CookieParam("userToken") Cookie cookie) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("reservation timestamp: " + timestamp.getTime());
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        if (cookie.getValue().equals("")) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.UNAUTHENTICATED_REQUEST)
                    .build());
        }

        AuthenticationDetail authentication = loadAuthenticationDetail(cookie.getValue());
        if (authentication == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.BAD_AUTHENTICATON_TOKEN)
                    .build());
        }

        Reservation reservation = entityManager.find(Reservation.class, reservationDTO.getId());
        if ((timestamp.getTime()- reservation.getTimestamp()) > (ConcertApplication.RESERVATION_EXPIRY_TIME_IN_SECONDS*1000)) {
            entityManager.remove(reservation);
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.EXPIRED_RESERVATION)
                    .build());
        }

        if (!reservation.getUser().hasCreditCard()) {
            throw new WebApplicationException(Response.status(Response
                    .Status.BAD_REQUEST)
                    .entity(Messages.CREDIT_CARD_NOT_REGISTERED)
                    .build());
        }

        entityManager.remove(reservation);
        Booking booking = new Booking(reservation.getConcert(), reservation.getDate(), reservation.getSeats(), reservation.getPriceBand());
        entityManager.persist(booking);
        transaction.commit();

        Response.ResponseBuilder builder;
        builder = Response.status(201);
        entityManager.close();

        return builder.build();

    }


    @POST
    @Path("/creditCard")
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public Response registerCreditCard(CreditCardDTO creditCardDTO, @CookieParam("userToken") Cookie cookie) {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        if (cookie.getValue().equals("")) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.UNAUTHENTICATED_REQUEST)
                    .build());
        }

        AuthenticationDetail authentication = loadAuthenticationDetail(cookie.getValue());
        if (authentication == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.BAD_AUTHENTICATON_TOKEN)
                    .build());
        }

        System.out.println("name:" + creditCardDTO.getName() + ", number:" + creditCardDTO.getNumber()+ ", type" + creditCardDTO.getType());
        CreditCard creditCard = Mapper.creditCardDTOToCreditCard(creditCardDTO);
        creditCard.showCardDetails();
        User user = authentication.getUser();
        user.setCreditCard(creditCard);
        entityManager.persist(creditCard);
        transaction.commit();

        Response.ResponseBuilder builder;
        builder = Response.status(201);
        entityManager.close();
        return builder.build();

    }

    @GET
    @Path("/bookings")
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public Response getBookings(@CookieParam("userToken") Cookie cookie) {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        if (cookie.getValue().equals("")) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.UNAUTHENTICATED_REQUEST)
                    .build());
        }


        AuthenticationDetail authentication = loadAuthenticationDetail(cookie.getValue());
        if (authentication == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(Messages.BAD_AUTHENTICATON_TOKEN)
                    .build());
        }

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b", Booking.class);
        List<Booking> bookings = query.getResultList();

        System.out.println("booking size: " + bookings.size());

        Set<BookingDTO> bookingDTOs = Mapper.bookingsToBookingDTOs(bookings);

        Response.ResponseBuilder builder;
        GenericEntity<Set<BookingDTO>> entity = new GenericEntity<Set<BookingDTO>> (bookingDTOs) { };
        builder = Response.ok(entity);

        entityManager.close();

        return builder.build();

    }
    @GET
    @Path("/performerImage")
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public Response getImageForPerformer(PerformerDTO performer) {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //load performer whose image is required from the data
        Performer performerDB = entityManager.find(Performer.class, performer.getId());

        // if the Performer has no image associated then throw an Exception
        if (performerDB.getImage() == null) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(Messages.NO_IMAGE_FOR_PERFORMER)
                    .build());
        }

        Response.ResponseBuilder builder;
        builder = Response.status(204);
        entityManager.close();

        return builder.build();


    }

    private NewCookie makeCookie(){

        NewCookie newCookie = new NewCookie("userToken", UUID.randomUUID().toString());
        return newCookie;
    }

    private AuthenticationDetail loadAuthenticationDetail(String token) {
        AuthenticationDetail authenticationDetail = entityManager.find(AuthenticationDetail.class, token);
        return authenticationDetail;
    }




}
