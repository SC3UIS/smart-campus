package com.uis.iot.common.utils;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uis.iot.common.exception.GatewayResponseException;
import com.uis.iot.common.exception.InternalException;

public class CommonUtils {

	private CommonUtils() {
		throw new IllegalArgumentException("Utility class");
	}

	private static final RestTemplate restTemplate = new RestTemplate();

	public static String mapToJson(final Object obj) throws JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	public static <T> T mapFromJson(final String json, final Class<T> clazz) throws IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	public static <T, E> ResponseEntity<T> getHTTPResponse(final String endpoint, final HttpMethod method, final Class<T> responseType,
			final E body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		final HttpEntity<E> entity = new HttpEntity<>(body, headers);
		return restTemplate.exchange(endpoint, method, entity, responseType);
	}

	public static <T> ResponseEntity<T> getHTTPResponse(final String endpoint, final HttpMethod method, final Class<T> responseType) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		final HttpEntity<T> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(endpoint, method, entity, responseType);
	}
	
	public static String receiveGatewayExceptions(final HttpStatusCodeException e) {
		GatewayResponseException error;
		try {
			error = CommonUtils.mapFromJson(e.getResponseBodyAsString(), GatewayResponseException.class);
		} catch (final IOException e1) {
			error = new GatewayResponseException(HttpStatus.I_AM_A_TEAPOT, "Ha ocurrido un error inesperado:" + e1.getMessage());
		}
		return error.getMessage();
	}

	/**
	 * Sends an email to the given email address.
	 * 
	 * @param to      the person who the mail will be sent to.
	 * @param subject the subject of the mail.
	 * @param text    the content of the mail.
	 */
	public static void sendEmail(final String to, final String subject, final String text) {
		final String username = "sscycloud@gmail.com";
		final String password = "vnrhgftlzhjvkrjz";

		final Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		final Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(text, "text/html; charset=utf-8");

			Transport.send(message);

		} catch (final MessagingException e) {
			throw new InternalException("Un error ocurrio enviando el email", e);
		}
	}

}
