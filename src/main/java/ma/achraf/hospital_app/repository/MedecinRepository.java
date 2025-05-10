package ma.achraf.hospital_app.repository;

import ma.achraf.hospital_app.entities.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
}
