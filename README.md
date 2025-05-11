# 🏥 Rapport Technique — TP Gestion Hospitalière avec Spring Boot

## Introduction

Dans le cadre de ce TP, nous avons conçu une application de gestion hospitalière en utilisant **Spring Boot**, **Spring Data JPA**, **Lombok**, et **MySQL**. L’objectif est de modéliser les différentes entités d’un hôpital, telles que les patients, les médecins, les rendez-vous et les consultations, et de les relier via des relations appropriées. Une gestion de rôles utilisateurs (`ADMIN`, `USER`) est également mise en place pour préparer une future couche de sécurité.

---

## Modélisation des Entités

### 1. `Patient`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    private int score;
}
```

### 2. `Medecin`

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Medecin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String email;
    private String specialite;

    @OneToMany(mappedBy = "medecin")
    private List<RendezVous> rendezVous;
}
```

### 3. `RendezVous`

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private STATUS status;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Medecin medecin;

    @OneToOne(mappedBy = "rendezVous")
    private Consultation consultation;
}
```

### 4. `Consultation`

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateConsultation;
    private String rapport;

    @OneToOne
    private RendezVous rendezVous;
}
```

### 5. `Utilisateur` & `Role`

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}
```

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;
}
```

### 6. Enum `STATUS`

```java
public enum STATUS {
    EN_ATTENTE,
    CONFIRME,
    ANNULE,
    TERMINE
}
```

---

## Configuration

### Fichier `application.properties`

```properties
spring.application.name=hospital_app
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
server.port=8080
```

---

## Implémentation avec `CommandLineRunner`

### Classe principale `HospitalAppApplication`

```java
@SpringBootApplication
public class HospitalAppApplication implements CommandLineRunner {

    @Autowired private PatientRepository patientRepository;
    @Autowired private MedecinRepository medecinRepository;
    @Autowired private RendezVousRepository rendezVousRepository;
    @Autowired private ConsultationRepository consultationRepository;

    public static void main(String[] args) {
        SpringApplication.run(HospitalAppApplication.class, args);
    }

    @Override
    public void run(String... args) {
        patientRepository.save(new Patient(null, "messi", new Date(), true, 10));
        patientRepository.save(new Patient(null, "hafid", new Date(), false, 20));
        patientRepository.save(new Patient(null, "Karim", new Date(), true, 5));

        List<Patient> patients = patientRepository.findAll();
        patients.forEach(p -> System.out.println(p.getNom()));

        Patient patient = patients.get(0);
        patient.setScore(99);
        patientRepository.save(patient);

        Long idToDelete = patients.get(1).getId();
        patientRepository.deleteById(idToDelete);

        Medecin medecin = Medecin.builder()
                .nom("Dr. Salma")
                .specialite("Cardiologie")
                .build();
        medecin = medecinRepository.save(medecin);

        RendezVous rdv = RendezVous.builder()
                .date(new Date())
                .status(STATUS.EN_ATTENTE)
                .patient(patient)
                .medecin(medecin)
                .build();
        rdv = rendezVousRepository.save(rdv);

        Consultation consultation = Consultation.builder()
                .dateConsultation(new Date())
                .rapport("Consultation initiale : état stable.")
                .rendezVous(rdv)
                .build();
        consultationRepository.save(consultation);
    }
}
```

---

### Classe `TestUtilisateurRole`

```java
@SpringBootApplication
public class TestUtilisateurRole implements CommandLineRunner {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(TestUtilisateurRole.class, args);
    }

    @Override
    public void run(String... args) {
        Role role1 = Role.builder().roleName("ADMIN").build();
        Role role2 = Role.builder().roleName("USER").build();

        roleRepository.save(role1);
        roleRepository.save(role2);

        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);

        Utilisateur utilisateur = Utilisateur.builder()
                .username("achraf")
                .password("password123")
                .roles(roles)
                .build();

        utilisateurRepository.save(utilisateur);
        System.out.println("✅ Utilisateur avec rôles créé avec succès !");
    }
}
```

---

##  Conclusion

Ce TP nous a permis de mettre en pratique plusieurs aspects de Spring Boot et JPA :

* Modélisation des entités avec des relations complexes (`@OneToMany`, `@ManyToOne`, `@OneToOne`, `@ManyToMany`).
* Configuration d'une base de données MySQL avec Hibernate.
* Utilisation de `CommandLineRunner` pour insérer et manipuler les données au démarrage.
* Introduction à la gestion des utilisateurs et des rôles.

Ce projet représente une base solide pour construire une application hospitalière complète, et peut facilement évoluer vers une architecture RESTful avec sécurité (Spring Security), pagination, filtres dynamiques, ou encore une interface frontend connectée via une API.

