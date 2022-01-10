package isa.FishingAdventure.repository;

import isa.FishingAdventure.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompliantRepository extends JpaRepository<Complaint, Integer> {
}
