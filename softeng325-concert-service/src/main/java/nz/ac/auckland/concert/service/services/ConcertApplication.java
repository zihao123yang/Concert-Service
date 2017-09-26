package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Seat;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JAX-RS Application subclass for the Concert Web service.
 * 
 * 
 *
 */
@ApplicationPath("/services")
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;

	private Set<Object> _singletons = new HashSet<Object>();
	private Set<Class<?>> _classes = new HashSet<Class<?>>();

	public ConcertApplication() {
		_singletons.add(new PersistenceManager());
		_singletons.add(new SubscriptionResource());
		_classes.add(ConcertResource.class);

		EntityManager em = null;

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			em.createQuery("delete from AuthenticationDetail").executeUpdate();
			em.createQuery("delete from USERS").executeUpdate();

			TypedQuery<Concert> concertsQuery = em.createQuery("select c from Concert c", Concert.class);
			List<Concert> concerts = concertsQuery.getResultList();

			for (Concert c : concerts) {
				for (LocalDateTime dateTime : c.getLocalDateTimes()) {
					for (SeatRow seatRow : SeatRow.values()) {
						int numberOfSeats = TheatreLayout.getNumberOfSeatsForRow(seatRow);
						for (int i = 1; i <=numberOfSeats; i++) {
							SeatNumber seatNumber = new SeatNumber(i);
							Seat seat = new Seat(seatRow, seatNumber, dateTime);
							em.persist(seat);
						}
					}
				}
			}

			em.flush();
			em.clear();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	@Override
	public Set<Object> getSingletons() {
		return _singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return _classes;
	}
}
