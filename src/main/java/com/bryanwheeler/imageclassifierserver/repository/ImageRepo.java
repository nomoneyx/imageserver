package com.bryanwheeler.imageclassifierserver.repository;

import com.bryanwheeler.imageclassifierserver.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface ImageRepo extends JpaRepository<ImageModel, Long> {
}
