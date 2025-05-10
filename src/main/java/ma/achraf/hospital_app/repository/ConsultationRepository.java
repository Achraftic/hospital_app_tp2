package ma.achraf.hospital_app.repository;

import ma.achraf.hospital_app.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
