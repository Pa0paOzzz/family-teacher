package com.familyteacher.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class AmapGeocodingService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${amap.enabled:false}")
    private boolean enabled;

    @Value("${amap.api-key:}")
    private String apiKey;

    @Value("${amap.geocode-url:https://restapi.amap.com/v3/geocode/geo}")
    private String geocodeUrl;

    public Optional<AmapGeocodeResult> geocode(String rawAddress) {
        if (!enabled || !StringUtils.hasText(apiKey) || !StringUtils.hasText(rawAddress)) {
            return Optional.empty();
        }

        String requestUrl = UriComponentsBuilder.fromUriString(geocodeUrl)
                .queryParam("key", apiKey)
                .queryParam("address", rawAddress.trim())
                .build()
                .toUriString();

        try {
            ResponseEntity<AmapGeocodeResponse> response = restTemplate.getForEntity(requestUrl, AmapGeocodeResponse.class);
            AmapGeocodeResponse body = response.getBody();
            if (body == null || !"1".equals(body.status) || body.geocodes == null || body.geocodes.isEmpty()) {
                return Optional.empty();
            }

            AmapGeocodeItem first = body.geocodes.get(0);
            AmapGeocodeResult result = new AmapGeocodeResult();
            result.setFormattedAddress(first.formattedAddress);
            result.setProvince(first.province);
            result.setCity(resolveCity(first));
            result.setDistrict(first.district);

            if (StringUtils.hasText(first.location)) {
                String[] parts = first.location.split(",");
                if (parts.length == 2) {
                    result.setLongitude(parseDouble(parts[0]));
                    result.setLatitude(parseDouble(parts[1]));
                }
            }

            return Optional.of(result);
        } catch (RestClientException ex) {
            return Optional.empty();
        }
    }

    private String resolveCity(AmapGeocodeItem item) {
        if (item.city instanceof String cityText && StringUtils.hasText(cityText)) {
            return cityText;
        }
        if (item.city instanceof List<?> cityList && !cityList.isEmpty()) {
            Object first = cityList.get(0);
            if (first instanceof String cityText && StringUtils.hasText(cityText)) {
                return cityText;
            }
        }
        return item.province;
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static class AmapGeocodeResult {
        private String province;
        private String city;
        private String district;
        private String formattedAddress;
        private Double longitude;
        private Double latitude;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AmapGeocodeResponse {
        public String status;
        public List<AmapGeocodeItem> geocodes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AmapGeocodeItem {
        public String province;
        public Object city;
        public String district;
        public String location;

        @JsonProperty("formatted_address")
        public String formattedAddress;
    }
}
