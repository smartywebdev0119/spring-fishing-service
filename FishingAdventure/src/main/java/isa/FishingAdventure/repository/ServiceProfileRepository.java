package isa.FishingAdventure.repository;

import isa.FishingAdventure.model.ServiceProfile;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface ServiceProfileRepository extends JpaRepository<ServiceProfile, Integer> {
}
