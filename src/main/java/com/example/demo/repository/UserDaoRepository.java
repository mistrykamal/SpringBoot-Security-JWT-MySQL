package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.DAOUser;

@Repository
public interface UserDaoRepository extends CrudRepository<DAOUser, Integer> {
}
