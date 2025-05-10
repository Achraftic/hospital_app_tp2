package ma.achraf.hospital_app;

import ma.achraf.hospital_app.entities.Role;
import ma.achraf.hospital_app.entities.Utilisateur;
import ma.achraf.hospital_app.repository.RoleRepository;
import ma.achraf.hospital_app.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class TestUtilisateurRole implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(TestUtilisateurRole.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Création des rôles ADMIN et USER
        Role role1 = Role.builder().roleName("ADMIN").build();
        Role role2 = Role.builder().roleName("USER").build();

        // Sauvegarde des rôles
        roleRepository.save(role1);
        roleRepository.save(role2);

        // Création d'un utilisateur
        List<Role> roles =  new ArrayList<>();


        roles.add(role1); // L'utilisateur aura le rôle ADMIN
        roles.add(role2); // L'utilisateur aura aussi le rôle USER

        Utilisateur utilisateur = Utilisateur.builder()
                .username("achraf")
                .password("password123")
                .roles(roles)
                .build();

        // Sauvegarde de l'utilisateur
        utilisateurRepository.save(utilisateur);

        System.out.println("Utilisateur avec rôles créé avec succès !");
    }
}
