package de.queisler.battleship.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.queisler.battleship.businessLogic.model.Player;
import de.queisler.battleship.data.exceptions.LoginException;
import de.queisler.battleship.data.exceptions.PlayerAlreadyExistException;
import de.queisler.battleship.data.repositories.PlayerRepository;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService
{
	@Autowired
	private PlayerRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Player savePlayer(Player player) throws PlayerAlreadyExistException
	{
		Player exPlayer = repository.findByUsername(player.getUsername());
		if (exPlayer != null)
			throw new PlayerAlreadyExistException("A player with the identical username already exists!");

		player.setPassword(passwordEncoder.encode(player.getPassword()));
		return repository.save(player);
	}

	@Override
	public Player login(String username, String password) throws LoginException
	{
		Player exPlayer = repository.findByUsername(username);
		if (exPlayer == null)
			throw new LoginException("A player with the username " + username + " does not exist!");
		if (!passwordEncoder.encode(password).equals(exPlayer.getPassword()))
			throw new LoginException("Wrong password!");
		// Spring authentication

		return exPlayer;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		Player exPlayer = repository.findByUsername(username);
		if (exPlayer == null)
			throw new UsernameNotFoundException("A player with the username " + username + " does not exist!");

		return exPlayer;
	}
}