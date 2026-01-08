package com.churninsight_dev.backend_api.service;

import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;
import com.churninsight_dev.backend_api.model.Customer;
import com.churninsight_dev.backend_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class PredictionServiceImpl implements PredictionService{
    private final RestTemplate restTemplate;
    @Value("${api.ds.url}")
    private String dsServiceUrl;

    public PredictionServiceImpl(CustomerRepository customerRepository, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PredictionResponse processPrediction(PredictionRequest request) {
        // 1. Llamar al microservicio Python para obtener la predicción real
        if (dsServiceUrl == null) {
            throw new IllegalStateException("Data Science service URL (dsServiceUrl) must not be null");
        }
        PredictionResponse response = restTemplate.postForObject(dsServiceUrl, request, PredictionResponse.class);

        // 2. CREAR Y GUARDAR EL CLIENTE EN LA BASE DE DATOS (Persistencia)
        Customer customer = new Customer();
        customer.setCustomerId(request.getCustomerId());
        customer.setAge(request.getAge());
        customer.setGender(request.getGender());
        customer.setSubscriptionType(request.getSubscriptionType());
        customer.setWatchHours(request.getWatchHours());
        customer.setLastLoginDays(request.getLastLoginDays());
        customer.setRegion(request.getRegion());
        customer.setDevice(request.getDevice());
        customer.setMonthlyFee(request.getMonthlyFee());
        customer.setPaymentMethod(request.getPaymentMethod());
        customer.setNumberOfProfiles(request.getNumberOfProfiles());
        customer.setAvgWatchTimePerDay(request.getAvgWatchTimePerDay());
        customer.setFavoriteGenre(request.getFavoriteGenre());


        // 2. Guardar historial de predicción eliminado (PredictionHistory ya no existe)

        // 4. Retornar la respuesta del microservicio Python
        return response;
    }
}
