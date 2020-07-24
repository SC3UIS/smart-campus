package com.uis.iot.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.repository.IUserRepository;
import com.uis.iot.admin.utils.Utils;
import com.uis.iot.common.exception.InternalException;
import com.uis.iot.common.exception.InvalidKeyException;
import com.uis.iot.common.exception.RecordExistsException;
import com.uis.iot.common.model.AppUserPassDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Logic to handle the management of Users.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 * 
 */
@Service
public class UserService implements IUserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);

	/**
	 * Used for executing operations on the database
	 */
	@Autowired
	private IUserRepository repository;

	@Override
	@Transactional
	public AppUser registerUser(final AppUser user) {
		if (repository.findByUsername(user.getUsername()).isPresent()) {
			throw new RecordExistsException("Ya existe un usuario con este nombre.");
		}
		if (repository.findByEmail(user.getEmail()).isPresent()) {
			throw new RecordExistsException("Ya existe un usuario con este correo.");
		}
		try {
			user.setPassword(Utils.hashPassword(user.getPassword()));
			user.setId(null);
			return repository.save(user);
		} catch (final DataAccessException e) {
			LOGGER.error("El usuario no pudo ser registrado.", e);
			throw new InternalException("El usuario no pudo ser registrado: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public AppUser editUser(final AppUser user, final long userId) {
		try {
			final AppUser foundUser = getUser(userId);
			foundUser.setName(user.getName());
			foundUser.setEmail(user.getEmail());
			if (!StringUtils.isEmpty(user.getPassword())) {
				foundUser.setPassword(Utils.hashPassword(user.getPassword()));
			}
			return repository.save(foundUser);
		} catch (final DataAccessException e) {
			LOGGER.error("El usuario no pudo ser editado.", e);
			throw new InternalException("El usuario no pudo ser editado: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AppUser authenticateUser(final AppUser user) {
		final AppUser foundUser = repository.findByUsername(user.getUsername())
				.orElseThrow(() -> new InvalidKeyException("El nombre de usuario o contraseña son incorrectos."));
		if (Utils.passwordsMatch(user.getPassword(), foundUser.getPassword())) {
			return foundUser;
		}

		throw new InvalidKeyException("El nombre de usuario o contraseña son incorrectos.");
	}

	@Override
	@Transactional
	public ResponseDTO deleteUser(final long userId) {
		try {
			getUser(userId);
			repository.deleteById(userId);
			return new ResponseDTO("Se ha eliminado el usuario correctamente.", true);
		} catch (final DataAccessException e) {
			LOGGER.error("El usuario no fue eliminado", e);
			throw new InternalException("No se ha eliminado ningún usuario: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AppUser getUser(final long userId) {
		return repository.findById(userId).orElseThrow(() -> new InvalidKeyException("Usuario no encontrado."));
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppUser> getAllUsers() {
		return (List<AppUser>) repository.findAll();
	}

	@Override
	@Transactional
	public ResponseDTO changePass(final AppUserPassDTO passDTO, final long userId) {
		try {
			final AppUser user = repository.findById(userId)
					.orElseThrow(() -> new InvalidKeyException("Usuario no encontrado."));

			if (!Utils.passwordsMatch(passDTO.getOldPass(), user.getPassword())) {
				LOGGER.error("La contraseña anterior no es correcta.");
				throw new InvalidKeyException("La contraseña anterior no es correcta.");
			}

			user.setPassword(Utils.hashPassword(passDTO.getNewPass()));
			repository.save(user);
			return new ResponseDTO("Contraseña cambiada exitosamente.", true);
		} catch (final DataAccessException e) {
			LOGGER.error("La contraseña no pudo ser cambiada.", e);
			throw new InternalException("La contraseña no pudo ser cambiada: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public ResponseDTO retrievePass(final String email) {
		final AppUser user = repository.findByEmail(email)
				.orElseThrow(() -> new InvalidKeyException("El correo no corresponde a ningún usuario."));
		final String newPass = Utils.randomString(8);
		user.setPassword(Utils.hashPassword(newPass));
		try {
			repository.save(user);
		} catch (final DataAccessException e) {
			LOGGER.error("La contraseña no pudo ser cambiada.", e);
			throw new InternalException("La contraseña no pudo ser cambiada: " + e.getMessage(), e);
		}
		Utils.sendPasswordEmail(user.getName(), user.getEmail(), newPass);
		return new ResponseDTO("La nueva contraseña ha sido enviada correctamente.", true);
	}
}
