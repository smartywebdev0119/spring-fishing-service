package isa.FishingAdventure.repository;

import isa.FishingAdventure.model.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCatagoryRepository extends JpaRepository<UserCategory, Integer> {
}
