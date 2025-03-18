package com.crafting.custom.woodwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crafting.custom.woodwork.entity.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails,Integer>{
	
	 UserDetails findByEmail(String email);

}
