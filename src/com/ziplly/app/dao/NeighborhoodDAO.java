package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.server.model.jpa.Neighborhood;

public interface NeighborhoodDAO {
	NeighborhoodDTO findById(Long neighborhoodId) throws NotFoundException;

	List<Neighborhood> getAll(int start, int end);

	// NeighborhoodDTO findFirstByPostalCode(int postalCode);
	List<Neighborhood> findAll();

	Long findTotalNeighborhoods(String countQuery);

	List<Neighborhood> findByPostalCode(String postalCode);

	/**
	 * Returns the list of all neighborhoods under that neighborhood.
	 * 
	 * @throws NotFoundException
	 */
	List<Neighborhood>
	    findAllDescendentNeighborhoods(Long neighborhoodId) throws NotFoundException;

	List<Neighborhood>
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

	List<Neighborhood> findNeighborhoodsByLocality(NeighborhoodDTO neighborhood);

  List<Neighborhood> findDescendentNeighborhoods(Long neighborhoodId) throws NotFoundException;

  /**
   * Given a neighborhood, it returns all neighborhoods within one level up from that.
   * @param neighborhoodId
   * @return
   */
  List<Long> getNeigborhoodIdsForCity(Long neighborhoodId);
}