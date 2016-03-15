package com.theironyard.services;//Created by KevinBozic on 3/15/16.

import com.theironyard.entities.Photo;
import org.springframework.data.repository.CrudRepository;

public interface PhotoRepository extends CrudRepository<Photo, Integer> {
}
