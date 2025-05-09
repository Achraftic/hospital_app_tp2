package ma.achraf.hospital_app.repository;

import ma.achraf.hospital_app.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    List<Patient> findByMalade(boolean malade);
}
