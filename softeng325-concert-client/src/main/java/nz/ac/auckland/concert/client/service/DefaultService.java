package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;


public class DefaultService implements ConcertService {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";
	private static String SUBSCRIPTION_SERVICE_URI = "http://localhost:10000/services/subscription";
	private String _cookieValue;

	// AWS S3 access credentials for concert images.
	private static final String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";
	private static final String AWS_SECRET_ACCESS_KEY = "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";

	// Name of the S3 bucket that stores images.
	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	private static final String USER_DIRECTORY = System
			.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
			+ FILE_SEPARATOR + "images";

	private NewsItemDTO _lastItemReceived;

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {

		Response response = null;
		Client client = ClientBuilder.newClient();

		try {

			// Make an invocation on a Concert URI and specify Java-
			// serialization as the required data format.
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/all").request()
					.accept(MediaType.APPLICATION_XML);

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.get();

			Set<ConcertDTO> concertDTOs = response.readEntity(new GenericType<Set<ConcertDTO>>(){});
			return concertDTOs;

		} catch (ServiceException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.SERVICE_UNAVAILABLE)
					.entity(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD)
					.build());
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {

			// Make an invocation on a Concert URI and specify Java-
			// serialization as the required data format.
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/allPerformers").request()
					.accept(MediaType.APPLICATION_XML);

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.get();

			Set<PerformerDTO> performerDTOs = response.readEntity(new GenericType<Set<PerformerDTO>>(){});
			return performerDTOs;

		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			// Prepare an invocation on the Concert service
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/user").request();

			// Make the service invocation via a HTTP POST message, and wait
			// for the response.
			response = builder.post(Entity.entity(newUser,
					MediaType.APPLICATION_XML));

			UserDTO user = null;

			// Check that the HTTP response code is 201 Created.
			int responseCode = response.getStatus();
			switch (responseCode) {
				case 400:
					System.out.println("hi");
					String errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
				case 201:
					user = response.readEntity(UserDTO.class);
					processCookieFromResponse(response);
			}
			System.out.println("cookie user" + _cookieValue);
			return user;

		} finally {
			// Close the Response object.
			response.close();
		}
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {

		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			// Prepare an invocation on the Concert service
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/authenticate").request();

			// Make the service invocation via a HTTP POST message, and wait
			// for the response.
			response = builder.put(Entity.entity(user,
					MediaType.APPLICATION_XML));


			// Check that the HTTP response code is 201 Created.
			int responseCode = response.getStatus();
			switch (responseCode) {
				case 401:
					String errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
				case 200:
					user = response.readEntity(UserDTO.class);
					processCookieFromResponse(response);

			}
			System.out.println("cookie auth" + _cookieValue);

			return user;

		} finally {
			// Close the Response object.
			response.close();
		}
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {

		Response response = null;
		//create new Client
		Client client = ClientBuilder.newClient();
		Image image = null;
		try {
			//prepare an Invocation to the ConcertResource method with the URI http://localhost:10000/services/concerts/performerImage
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/performerImage").request();
			//make the request to the service
			response = builder.get();

			//get the status code returned by the response from the ConcertResource service
			int responseCode = response.getStatus();
			switch (responseCode) {
				case 400:
					//if a bad request was made, throw ServiceException with the Message from the response
					String errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
				case 204:
					//download image if status code is 204
					image = downloadImage(performer);

			}
		} finally {
			response.close();
		}
		return image;

	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();
		System.out.println("cookie" + _cookieValue);
		try {

			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/reserve").request()
					.accept(MediaType.APPLICATION_XML);

			addCookieToInvocation(builder);

			response = builder.post(Entity.entity(reservationRequest, MediaType.APPLICATION_XML));

			ReservationDTO reservation = null;
			int responseCode = response.getStatus();
			switch (responseCode) {
				case 400:
					String errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
				case 401:
					errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
				case 201:
					reservation = response.readEntity(ReservationDTO.class);
			}

			return reservation;

		} finally {
			response.close();
		}
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {

		Response response = null;
		Client client = ClientBuilder.newClient();

		try {

			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/confirm").request();
			addCookieToInvocation(builder);
			response = builder.post(Entity.entity(reservation, MediaType.APPLICATION_XML));

			int responseCode = response.getStatus();
			switch (responseCode) {
				case 400:
					String errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
				case 401:
					errorMessage = response.readEntity(String.class);
					throw new ServiceException(errorMessage);
			}
		} finally {
			response.close();
		}
		
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/creditCard").request()
					.accept(MediaType.APPLICATION_XML);
			addCookieToInvocation(builder);
			response = builder.post(Entity.entity(creditCard, MediaType.APPLICATION_XML));

			int responseCode = response.getStatus();
			if (responseCode == 401) {

				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			}
		} finally {
			response.close();
		}
		
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			Invocation.Builder builder = client.target(WEB_SERVICE_URI + "/bookings").request()
					.accept(MediaType.APPLICATION_XML);
			addCookieToInvocation(builder);
			response = builder.get();

			Set<BookingDTO> bookings = null;
			int responseCode = response.getStatus();
			if (responseCode == 401) {
				String errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
			} else if(responseCode == 200) {
				bookings = response.readEntity(new GenericType<Set<BookingDTO>>() {});
			}

			return bookings;

		} finally {
			response.close();
		}
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		Client client = ClientBuilder.newClient();
		final WebTarget target = client.target(SUBSCRIPTION_SERVICE_URI + "/subscribe");
		target.request()
				.cookie("subscriptionCheck", _cookieValue)
				.async()
				.get(new InvocationCallback<List<NewsItemDTO>>() {
					public void completed(List<NewsItemDTO> newsItemDTOs) {
						_lastItemReceived = newsItemDTOs.get(newsItemDTOs.size() - 1);

						target.request()
								.cookie("subscriptionCheck", _cookieValue + "/" + _lastItemReceived.getTimetamp().toString())
								.async()
								.get(this);
						for (NewsItemDTO newsItem : newsItemDTOs) {
							listener.newsItemReceived(newsItem);
						}
					}

					public void failed(Throwable t) {
					}
				});
		
	}

	@Override
	public void cancelSubscription() {
		Client client = ClientBuilder.newClient();
		final WebTarget target = client.target(SUBSCRIPTION_SERVICE_URI + "/unSubscribe");
		target.request().cookie("subscriptionCheck", _cookieValue).async().delete();



	}




	private void addCookieToInvocation(Invocation.Builder builder) {
		builder.cookie("userToken", _cookieValue);
	}

	private void processCookieFromResponse(Response response) {
		String token = response.getCookies().get("userToken").getValue();
		_cookieValue = token;
	}

	private Image downloadImage(PerformerDTO performer) {

		File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
		downloadDirectory.mkdir();

		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
				AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.AP_SOUTHEAST_2)
				.withCredentials(
						new AWSStaticCredentialsProvider(awsCredentials))
				.build();
		BufferedImage image = null;
		try {
			String imageName = performer.getImageName();
			File imageFile = new File(imageName);

			S3Object o = s3.getObject(AWS_BUCKET, imageName);
			S3ObjectInputStream s3ObjectInputStream = o.getObjectContent();
			FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
			byte[] read_buf = new byte[1024];
			int read_len = 0;
			while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
				fileOutputStream.write(read_buf, 0, read_len);
			}
			s3ObjectInputStream.close();
			fileOutputStream.close();
			image = ImageIO.read(imageFile);

		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return image;
	}

}
