Voici un guide complet pour accomplir les différentes étapes demandées :

## 1. Créer un projet Spring Initializer avec les dépendances JPA, H2, Spring Web et Lombok

### Étapes :

1. Accédez à [Spring Initializr](https://start.spring.io/).
2. Sélectionnez les options suivantes :

    * **Project** : Maven Project (ou Gradle Project, selon votre préférence)
    * **Language** : Java
    * **Spring Boot** : 2.x.x (choisissez la dernière version stable)
    * **Dependencies** :

        * **Spring Web**
        * **Spring Data JPA**
        * **H2 Database**
        * **Lombok**
3. Cliquez sur **Generate**, puis téléchargez et décompressez le projet généré.

## 2. Créer l'entité JPA `Patient`

Créez une classe `Patient` dans le répertoire `src/main/java/ma/achraf/hospital_app/entities/`.

### Patient.java

```java
package ma.achraf.hospital_app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;
    
    private boolean malade;
    
    private int score;
}
```

* **@Entity** indique que cette classe est une entité JPA.
* **@Id** marque l'attribut `id` comme clé primaire.
* **@GeneratedValue** spécifie la génération automatique de l'ID.
* **@Temporal(TemporalType.DATE)** pour spécifier que `dateNaissance` est une date.

## 3. Configurer l'unité de persistance dans le fichier `application.properties`

Ouvrez le fichier `src/main/resources/application.properties` et ajoutez la configuration suivante pour l'unité de persistance avec H2 :

### application.properties

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

Si vous migrez vers MySQL, voici la configuration à ajouter :

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db
spring.datasource.username=root
spring.datasource.password=root_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

Assurez-vous d'avoir installé MySQL et que la base de données `hospital_db` existe.

## 4. Créer l'interface JPA Repository basée sur Spring Data

Créez l'interface `PatientRepository` dans le répertoire `src/main/java/ma/achraf/hospital_app/repository/`.

### PatientRepository.java

```java
package ma.achraf.hospital_app.repository;

import ma.achraf.hospital_app.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByMalade(boolean malade);
}
```

* **JpaRepository** offre des méthodes CRUD par défaut.
* La méthode `findByMalade` permet de rechercher les patients en fonction de leur statut malade.

## 5. Tester quelques opérations de gestion de patients

Créez un fichier `CommandLineRunner` dans le répertoire `src/main/java/ma/achraf/hospital_app/` pour tester les opérations CRUD.

### TestPatientOperations.java

```java
package ma.achraf.hospital_app;

import ma.achraf.hospital_app.entities.Patient;
import ma.achraf.hospital_app.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class TestPatientOperations implements CommandLineRunner {

    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(TestPatientOperations.class, args);
    }

    @Override
    public void run(String... args) {
        // Ajouter des patients
        patientRepository.save(new Patient(null, "Achraf", new Date(), true, 10));
        patientRepository.save(new Patient(null, "Lina", new Date(), false, 20));

        // Consulter tous les patients
        List<Patient> patients = patientRepository.findAll();
        System.out.println("Tous les patients :");
        patients.forEach(p -> System.out.println(p.getNom()));

        // Consulter un patient par ID
        Patient patient = patientRepository.findById(1L).orElse(null);
        if (patient != null) {
            System.out.println("Patient trouvé : " + patient.getNom());
        }

        // Chercher des patients malades
        List<Patient> malades = patientRepository.findByMalade(true);
        System.out.println("Patients malades :");
        malades.forEach(p -> System.out.println(p.getNom()));

        // Mettre à jour un patient
        if (patient != null) {
            patient.setScore(100);
            patientRepository.save(patient);
            System.out.println("Patient mis à jour : " + patient.getNom());
        }

        // Supprimer un patient
        if (!patients.isEmpty()) {
            Long idToDelete = patients.get(0).getId();
            patientRepository.deleteById(idToDelete);
            System.out.println("Patient supprimé avec ID : " + idToDelete);
        }
    }
}
```

### Ce que ce code fait :

1. **Ajouter des patients** : Création de nouveaux patients.
2. **Consulter tous les patients** : Affichage de tous les patients enregistrés.
3. **Consulter un patient par ID** : Recherche d'un patient par son identifiant.
4. **Chercher des patients malades** : Recherche des patients dont le statut est malade.
5. **Mettre à jour un patient** : Modification du score d'un patient.
6. **Supprimer un patient** : Suppression d'un patient.

## 6. Migrer de H2 Database vers MySQL

Comme mentionné précédemment, vous devez installer MySQL, créer une base de données (par exemple `hospital_db`), et ajuster les propriétés dans `application.properties` pour se connecter à MySQL. Vous aurez également besoin du driver MySQL :

Ajoutez la dépendance dans le fichier `pom.xml` pour MySQL :

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

## 7. Reprendre les exemples du Patient, Médecin, Rendez-vous, Consultation, Utilisateurs et Rôles

### Exemple d'entités supplémentaires :

#### Médecin.java

```java
package ma.achraf.hospital_app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private String specialite;
}
```

#### RendezVous.java

```java
package ma.achraf.hospital_app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Medecin medecin;

    @Temporal(TemporalType.DATE)
    private Date dateRendezVous;
}
```

#### Consultation.java

```java
package ma.achraf.hospital_app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private RendezVous rendezVous;

    @Temporal(TemporalType.DATE)
    private Date dateConsultation;

    private String diagnostic;
}
```

#### Utilisateur.java

```java
package ma.achraf.hospital_app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;

    @ManyToMany
    private Set<Role> roles;
}
```

#### Role.java

```java
package ma.achraf.hospital_app.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
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

Ensuite, créez des repositories pour ces entités et ajustez votre `CommandLineRunner` pour tester ces nouvelles entités.

## Conclusion

Avec ces étapes, vous avez créé un projet Spring Boot pour gérer les patients, les médecins, les rendez-vous, les consultations, les utilisateurs et les rôles. Vous avez aussi migré de H2 vers MySQL.
