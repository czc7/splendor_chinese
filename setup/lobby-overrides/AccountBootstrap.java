package eu.kartoffelquadrat.ls.accountmanager.config;

import eu.kartoffelquadrat.ls.accountmanager.model.Player;
import eu.kartoffelquadrat.ls.accountmanager.model.PlayerRepository;
import eu.kartoffelquadrat.ls.accountmanager.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Ensures the deployment's default player accounts exist. */
@Component
public class AccountBootstrap implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountBootstrap(PlayerRepository playerRepository, BCryptPasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        savePlayerWithSimplePassword("user1", "00DD44");
        savePlayerWithSimplePassword("user2", "2288EE");
        savePlayerWithSimplePassword("user3", "EE2222");
        savePlayerWithSimplePassword("user4", "C4A500");
    }

    private void savePlayerWithSimplePassword(String name, String colour) {
        Player player = playerRepository.findById(name)
                .orElseGet(() -> new Player(name, colour, "", Role.ROLE_PLAYER));
        player.setPassword(passwordEncoder.encode("123456"));
        player.setRole(Role.ROLE_PLAYER);
        playerRepository.save(player);
    }
}
