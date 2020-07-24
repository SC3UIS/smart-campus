package com.uis.iot.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uis.iot.admin.entity.AppUser;
import com.uis.iot.admin.service.IUserService;
import com.uis.iot.common.model.AppUserDTO;
import com.uis.iot.common.model.AppUserPassDTO;
import com.uis.iot.common.model.ResponseDTO;

/**
 * Entry point for all User related REST services.
 * 
 * @author felipe.estupinan
 * @author kevin.arias
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {

	@Autowired
	private IUserService service;

	private static final String USERNAME_REQUIRED = "Username requerido.";
	private static final String PASSWORD_REQUIRED = "Contraseña requerida.";
	private static final String EMAIL_REQUIRED = "Correo requerido.";
	private static final String NAME_REQUIRED = "Nombre requerido.";

	/**
	 * Saves a new user to the database.
	 * 
	 * @param userDTO dto of the user who will be registered
	 * @return user dto, but with an assigned id
	 */
	@PostMapping(path = "/user", consumes = "application/json", produces = "application/json")
	public ResponseEntity<AppUserDTO> register(@RequestBody final AppUserDTO userDTO) {
		Assert.hasText(userDTO.getUsername(), USERNAME_REQUIRED);
		Assert.hasText(userDTO.getPassword(), PASSWORD_REQUIRED);
		Assert.hasText(userDTO.getEmail(), EMAIL_REQUIRED);
		Assert.hasText(userDTO.getName(), NAME_REQUIRED);

		return new ResponseEntity<>(service.registerUser(AppUser.fromDTO(userDTO)).toDTO(), HttpStatus.CREATED);
	}

	/**
	 * Updates the information of a user in the database.
	 * 
	 * @param userDTO dto of the user who will be registered
	 * @param idUser  id of the user to be updated
	 * @return user dto with the updated information
	 */
	@PutMapping(path = "/user/{idUser}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<AppUserDTO> edit(@RequestBody final AppUserDTO userDTO, @PathVariable final long idUser) {
		Assert.hasText(userDTO.getUsername(), USERNAME_REQUIRED);
		Assert.hasText(userDTO.getEmail(), EMAIL_REQUIRED);
		Assert.hasText(userDTO.getName(), NAME_REQUIRED);

		return new ResponseEntity<>(service.editUser(AppUser.fromDTO(userDTO), idUser).toDTO(), HttpStatus.OK);
	}

	/**
	 * Updates the password of a user in the database.
	 * 
	 * @param passDTO dto that contains the old password (for authentication) and
	 *                the new one (for update)
	 * @param idUser  the id of the user to be updated
	 * @return response message indicating whether the password was changed
	 */
	@PutMapping(path = "/password/{idUser}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseDTO> changePass(@RequestBody final AppUserPassDTO passDTO,
			@PathVariable final long idUser) {
		Assert.hasText(passDTO.getOldPass(), PASSWORD_REQUIRED);
		Assert.hasText(passDTO.getNewPass(), NAME_REQUIRED);

		return new ResponseEntity<>(service.changePass(passDTO, idUser), HttpStatus.OK);
	}

	/**
	 * Verifies a user's credentials.
	 * 
	 * @param userDTO dto of the user trying to log in
	 * @return user information (if the credentials are correct)
	 */
	@PostMapping(path = "/authentication", consumes = "application/json", produces = "application/json")
	public ResponseEntity<AppUserDTO> authenticate(@RequestBody final AppUserDTO userDTO) {
		Assert.hasText(userDTO.getUsername(), USERNAME_REQUIRED);
		Assert.hasText(userDTO.getPassword(), PASSWORD_REQUIRED);
		return new ResponseEntity<>(service.authenticateUser(AppUser.fromDTO(userDTO)).toDTO(), HttpStatus.ACCEPTED);
	}

	/**
	 * Deletes a user from the database.
	 * 
	 * @param idUser id of the user to be deleted
	 * @return response message indicating whether the user was deleted
	 */
	@DeleteMapping(value = "/user/{idUser}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable final long idUser) {
		return new ResponseEntity<>(service.deleteUser(idUser), HttpStatus.OK);
	}

	/**
	 * Gets user by id.
	 * 
	 * @return a {@link AppUserDTO}.
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<AppUserDTO> getUser(@PathVariable final long userId) {
		return new ResponseEntity<>(service.getUser(userId).toDTO(), HttpStatus.OK);
	}

	/**
	 * Gets all users in the database.
	 * 
	 * @return a list of users.
	 */
	@GetMapping("")
	public ResponseEntity<List<AppUserDTO>> getAllUsers() {
		return new ResponseEntity<>(service.getAllUsers().stream().map(AppUser::toDTO).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * Sends an email to the user with their new password.
	 * 
	 * @param email email of the requesting user
	 * @return response message indicating whether the mail was sent
	 */
	@GetMapping("/pass/{email}")
	public ResponseEntity<ResponseDTO> retrievePass(@PathVariable final String email) {
		return new ResponseEntity<>(service.retrievePass(email), HttpStatus.OK);
	}

}
