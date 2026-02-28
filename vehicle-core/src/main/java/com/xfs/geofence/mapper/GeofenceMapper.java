package com.xfs.geofence.mapper;

import com.xfs.geofence.pojo.dto.GeofenceQuery;
import com.xfs.geofence.pojo.entity.Geofence;
import com.xfs.geofence.pojo.vo.GeofenceVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceMapper {
    List<GeofenceVO> selectGeofence(GeofenceQuery geofenceQuery);

    void update(Geofence geofence);

    void deleteById(Long id);

    void insert(Geofence geofence);
}
