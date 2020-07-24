package com.uis.iot.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.entity.Application;
import com.uis.iot.admin.entity.Gateway;
import com.uis.iot.admin.repository.IApplicationRepository;
import com.uis.iot.admin.repository.IUserRepository;
import com.uis.iot.admin.utils.Utils;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Logic to handle the management of Applications.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
@Service
public class ApplicationService implements IApplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private IApplicationRepository applicationRepository;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private GatewayService gatewayService;

	@Override
	@Transactional
	public Application registerApplication(final Application application) {
		@SuppressWarnings("unused")
		final AppUser user = userRepository.findById(application.getAppUser().getId())
				.orElseThrow(() -> new InvalidKeyException("El usuario no existe."));
		try {
			return applicationRepository.save(application);
		} catch (final DataAccessException e) {
			LOGGER.error("La aplicación no pudo ser registrada. ", e);
			throw new InternalException("La aplicación no pudo ser registrada: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public Application editApplication(final Application application) {
		@SuppressWarnings("unused")
		final AppUser user = userRepository.findById(application.getAppUser().getId())
				.orElseThrow(() -> new InvalidKeyException("El usuario no existe."));
		try {
			final Application foundApplication = applicationRepository.findById(application.getId())
					.orElseThrow(() -> new InvalidKeyException("La aplicación no existe."));
			foundApplication.setName(application.getName());
			foundApplication.setDescription(application.getDescription());
			return applicationRepository.save(foundApplication);
		} catch (final DataAccessException e) {
			LOGGER.error("La aplicación no pudo ser actualizada. ", e);
			throw new InternalException("La aplicación no pudo ser actualizada: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ResponseDTO deleteApplication(final Long appId) {
		try {
			@SuppressWarnings("unused")
			final Application application = applicationRepository.findById(appId)
					.orElseThrow(() -> new InvalidKeyException("La aplicación no existe."));
			applicationRepository.deleteById(appId);
			return new ResponseDTO("La aplicación fue eliminada correctamente.", true);
		} catch (final DataAccessException e) {
			LOGGER.error("La aplicación no pudo ser eliminada. ", e);
			throw new InternalException("La aplicación no pudo ser eliminada. " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Application> getApplicationsByUserId(final long userId) {
		final AppUser user = userRepository.findById(userId)
				.orElseThrow(() -> new InvalidKeyException("El usuario no existe"));

		return user.isAdmin() ? (List<Application>) applicationRepository.findAll(Utils.NAME_ASC_SORT)
				: applicationRepository.findByUserId(userId, Utils.NAME_ASC_SORT);
	}

	@Override
	@Transactional(readOnly = true)
	public Application getApplicationById(final long appId) {
		final Application application = applicationRepository.findById(appId)
				.orElseThrow(() -> new InvalidKeyException("La aplicación no existe"));
		application.getGateways();
		return application;
	}

	@Override
	@Transactional
	public void assignGatewayToApplication(final long applicationId, final long gatewayId, final boolean assign) {
		final Application application = getApplicationById(applicationId);
		final Gateway gateway = gatewayService.getGateway(gatewayId);
		if (assign) {
			application.getGateways().add(gateway);
		} else {
			application.getGateways().remove(gateway);
		}
		try {
			applicationRepository.save(application);
		} catch (final DataAccessException e) {
			final String message = "La aplicación no pudo ser " + (assign ? "asignada" : "desasignada");
			LOGGER.error(message, e);
			throw new InternalException(message, e);
		}

	}

}
