package com.xfs.geofence.service;

import com.xfs.geofence.pojo.dto.GeofenceQuery;
import com.xfs.geofence.pojo.dto.GeofenceSaveParam;
import com.xfs.geofence.pojo.vo.GeofenceVO;

import java.util.List;

public interface GeofenceService {
    List<GeofenceVO> selectGeofence(GeofenceQuery geofenceQuery);

    void updateStatus(Long geofenceId, String status);

    void deleteGeofence(Long id);

    void saveGeofence(GeofenceSaveParam geofenceSaveParam);
}
