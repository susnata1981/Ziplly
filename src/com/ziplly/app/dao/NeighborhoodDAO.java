package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.NeighborhoodDTO;

public interface NeighborhoodDAO {
	NeighborhoodDTO findById(Long neighborhoodId) throws NotFoundException;

	List<Neighborhood> getAll(int start, int end);

	// NeighborhoodDTO findFirstByPostalCode(int postalCode);
	List<NeighborhoodDTO> findAll();

	Long findTotalNeighborhoods(String countQuery);

	List<NeighborhoodDTO> findByPostalCode(String postalCode);

	/**
	 * Returns the list of all neighborhoods under that neighborhood.
	 * 
	 * @throws NotFoundException
	 */
	List<NeighborhoodDTO>
	    findAllDescendentNeighborhoods(Long neighborhoodId) throws NotFoundException;

	List<NeighborhoodDTO>
	    findAllDescendentNeighborhoodsIncludingItself(Long neighborhoodId) throws NotFoundException;

	void update(Neighborhood neighborhood);

	void save(Neighborhood neighborhood);

	void delete(Long neighborhoodId);

	/**
	 * Finds neighborhood by NAME
	 * @param neighborhood
	 * @return 
	 */
	NeighborhoodDTO findOrCreateNeighborhood(NeighborhoodDTO neighborhood);

	List<NeighborhoodDTO> findNeighborhoodsByLocality(NeighborhoodDTO neighborhood);
}