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
public class HospitalAppApplication implements CommandLineRunner {

    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(HospitalAppApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 🔹 Créer et sauvegarder des patients
        patientRepository.save(new Patient(null, "Achraf", new Date(), true, 10));
        patientRepository.save(new Patient(null, "Lina", new Date(), false, 20));
        patientRepository.save(new Patient(null, "Karim", new Date(), true, 5));

        // 🔹 Consulter tous les patients
        System.out.println("🔍 Liste de tous les patients :");
        List<Patient> patients = patientRepository.findAll();
        patients.forEach(p -> {
            System.out.println(p.getId() + " | " + p.getNom() + " | " + p.getScore() + " | malade: " + p.isMalade());
        });

        // 🔹 Consulter un seul patient par ID
        Long id = patients.get(0).getId(); // par exemple le premier
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient != null) {
            System.out.println("\n👤 Patient trouvé : " + patient.getNom());
        }

        // 🔹 Chercher des patients malades
        System.out.println("\n🧪 Patients malades :");
        List<Patient> malades = patientRepository.findByMalade(true);
        malades.forEach(p -> System.out.println(p.getNom()));

        // 🔹 Mettre à jour un patient
        if (patient != null) {
            patient.setScore(99);
            patientRepository.save(patient);
            System.out.println("\n✅ Patient mis à jour : " + patient.getNom() + ", nouveau score: " + patient.getScore());
        }

        // 🔹 Supprimer un patient
        if (!patients.isEmpty()) {
            Long idToDelete = patients.get(1).getId(); // par exemple le deuxième
            patientRepository.deleteById(idToDelete);
            System.out.println("\n🗑️ Patient supprimé avec ID : " + idToDelete);
        }
    }
}
