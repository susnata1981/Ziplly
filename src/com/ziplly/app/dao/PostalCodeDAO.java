package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.PostalCode;
import com.ziplly.app.model.PostalCodeDTO;

public interface PostalCodeDAO {
        PostalCodeDTO findById(Long postalCodeId) throws NotFoundException;
        List<PostalCode> getAll(int start, int end);
        List<PostalCodeDTO> findAll();
        Long findTotalPostalCodes(String countQuery);
}