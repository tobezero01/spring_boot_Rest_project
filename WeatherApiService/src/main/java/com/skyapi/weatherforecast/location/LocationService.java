package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LocationService {

    private LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location add(Location location) {
        return locationRepository.save(location);
    }
    public boolean existsByCode(String code) {
        return locationRepository.existsById(code); // Hoặc sử dụng cách khác để kiểm tra mã địa điểm
    }

    @Deprecated
    public List<Location> list() {
        return locationRepository.findUnTrashed();
    }

    @Deprecated
    public Page<Location> listByPage(int pageNum, int pageSize, String sortField) {
        Sort sort = Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        return locationRepository.findUnTrashed(pageable);
    }

    public Page<Location> listByPage(int pageNum, int pageSize, String sortField, Map<String, Object> filterFields) {
        Sort sort = Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        return locationRepository.listWithFilter(pageable, filterFields);
    }

    public Location get(String code) {
        return locationRepository.findByCode(code);
    }

    public Location update(Location locationInRequest) throws LocationNotFoundException {
        String code = locationInRequest.getCode();

        Location locationInDB = locationRepository.findByCode(code);

        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given code :" + code);
        }

        locationInDB.setCityName(locationInRequest.getCityName());
        locationInDB.setRegionName(locationInRequest.getRegionName());
        locationInDB.setCountryName(locationInRequest.getCountryName());
        locationInDB.setCountryCode(locationInRequest.getCountryCode());
        locationInDB.setEnabled(locationInRequest.isEnabled());

        return locationRepository.save(locationInDB);
    }

    public void trashByCode(String code) throws LocationNotFoundException {
        if (!locationRepository.existsById(code)) {
            throw new LocationNotFoundException("No location found with the given code: " + code);
        }
        locationRepository.trashByCode(code);
    }
}
