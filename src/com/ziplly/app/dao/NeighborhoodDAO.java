package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.NeighborhoodDTO;

public interface NeighborhoodDAO {
	NeighborhoodDTO findById(Long neighborhoodId) throws NotFoundException;
	List<Neighborhood> getAll(int start, int end);
	NeighborhoodDTO findFirstByPostalCode(int postalCode);
	List<NeighborhoodDTO> findByPostalCode(int postalCode);
	List<NeighborhoodDTO> findAll();
	Long findTotalNeighborhoods(String countQuery);
	List<NeighborhoodDTO> findByPostalCode(String postalCode);
}