package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.service.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zihaoyang on 20/09/17.
 */
public class Mapper {

    public static Set<ConcertDTO> concertToConcertDTO(List<Concert> concerts) {

        Set<ConcertDTO> concertDTOs = new HashSet<>();

        for (Concert c : concerts) {

            ConcertDTO concertDTO;
            Set<Long> performerIds = new HashSet<>();

            for (Performer p : c.getPerformers()) {
                performerIds.add(p.getId());
            }
            concertDTO = new ConcertDTO(c.getId(), c.getTitle(), c.getLocalDateTimes(), c.getConcertTarifs(), performerIds);
            concertDTOs.add(concertDTO);
        }

        return concertDTOs;
    }


    public static Set<PerformerDTO> performerToConcertDTO(List<Performer> performers) {

        Set<PerformerDTO> performerDTOs = new HashSet<>();

        for (Performer p : performers) {
            PerformerDTO performerDTO;
            Set<Long> concertIds = new HashSet<>();

            for (Concert c : p.getConcerts()) {
                concertIds.add(c.getId());
            }
            performerDTO = new PerformerDTO(p.getId(), p.getName(), p.getImage(), p.getGenre(), concertIds);
            performerDTOs.add(performerDTO);

        }

        return performerDTOs;
    }

    public static User userDTOToUser(UserDTO dto) {

        User user = new User(dto.getUsername(), dto.getPassword(),  dto.getFirstname(), dto.getLastname());
        return user;
    }

//    public static Reservation reservationDTOToReservation(ReservationRequestDTO requestDTO, Set<Seat> reservedSeats, Concert concert) {
//
//        Reservation reservation = new Reservation(requestDTO.getNumberOfSeats(), requestDTO.getSeatType(), requestDTO.getDate(), reservedSeats, concert);
//        return reservation;
//    }

    public static Set<SeatDTO> seatsToSeatDTOs(Set<Seat> seats) {
        Set<SeatDTO> seatDTOs = new HashSet<>();
        for (Seat s : seats) {
            SeatDTO seatDTO = new SeatDTO(s.getRow(), s.getNumber());
            seatDTOs.add(seatDTO);
        }

        return seatDTOs;
    }

    public static Set<Seat> seatDTOsToSeats(Set<SeatDTO> seatDTOS, LocalDateTime date) {
        Set<Seat> seats = new HashSet<>();
        for (SeatDTO s : seatDTOS) {
            Seat seat = new Seat(s.getRow(), s.getNumber(), date);
            seats.add(seat);
        }
        return seats;
    }

    public static CreditCard creditCardDTOToCreditCard(CreditCardDTO dto) {

        CreditCard creditCard = new CreditCard(dto.getType(), dto.getName(), dto.getNumber(), dto.getExpiryDate());
        return creditCard;
    }

    public static Set<BookingDTO> bookingsToBookingDTOs(List<Booking> bookings) {

        Set<BookingDTO> bookingDTOs = new HashSet<>();

        for (Booking b: bookings) {
            Concert concert = b.getConcert();
            Set<SeatDTO> seatDTOs = seatsToSeatDTOs(b.getSeats());
            BookingDTO bookingDTO = new BookingDTO(concert.getId(), concert.getTitle(), b.getDate(), seatDTOs, b.getPriceBand());
            bookingDTOs.add(bookingDTO);
        }

        return bookingDTOs;
    }

    public static NewsItem newsItemDTOToNewsItem(NewsItemDTO dto) {

        NewsItem newsItem = new NewsItem(dto.getId(), dto.getTimetamp(), dto.getContent());
        return newsItem;
    }

    public static List<NewsItemDTO> newsItemTONewsItemDTO(List<NewsItem> newsItems) {
        List<NewsItemDTO> newsItemsDTO = new ArrayList<>();
        for (NewsItem n : newsItems) {
            NewsItemDTO newsItemDTO = new NewsItemDTO(n.getId(), n.getTimeStamp(), n.getContent());
            newsItemsDTO.add(newsItemDTO);
        }

        return newsItemsDTO;
    }


}
