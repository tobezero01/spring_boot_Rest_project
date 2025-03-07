package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.exception.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class HourlyWeatherService {
    private HourlyWeatherRepository hourlyWeatherRepository;
    private LocationRepository locationRepository;

    public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepository,
                                LocationRepository locationRepository) {
        this.hourlyWeatherRepository = hourlyWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
        Location locationInDB = locationRepository.findByCountryCodeAndCityName(
                location.getCountryCode(), location.getCityName()
        );
        if (locationInDB == null) {
            throw new LocationNotFoundException("Location not found with country code and city name");
        }

        return hourlyWeatherRepository.findByLocationCode(locationInDB.getCode(), currentHour);
    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) throws LocationNotFoundException {
        Location locationInDB = locationRepository.findByCode(locationCode);
        if (locationInDB == null) {
            throw new LocationNotFoundException("Location not found with the given code");
        }

        return hourlyWeatherRepository.findByLocationCode(locationCode, currentHour);
    }

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeathersRequest) throws LocationNotFoundException {
        Location location = locationRepository.findByCode(locationCode);

        if (location == null) {
            throw new LocationNotFoundException("Location not found with the given code");
        }
        for (HourlyWeather hourlyWeather : hourlyWeathersRequest) {
            hourlyWeather.getId().setLocation(location);
        }

        List<HourlyWeather> hourlyWeathersInDB = location.getListHourlyWeather();
        List<HourlyWeather> hourlyWeathersToBeRemoved = new ArrayList<>();

        for (HourlyWeather item : hourlyWeathersInDB) {
            if (!hourlyWeathersRequest.contains(item)) {
                hourlyWeathersToBeRemoved.add(item.getShallowCopy());
            }
        }

        for (HourlyWeather item : hourlyWeathersToBeRemoved) {
            hourlyWeathersInDB.remove(item);
        }
        return hourlyWeatherRepository.saveAll(hourlyWeathersRequest);
    }
}
