package com.churninsight.backend_api.service;

import com.churninsight.backend_api.dto.PredictionRequest;
import com.churninsight.backend_api.dto.PredictionResponse;
import com.churninsight.backend_api.model.Customer;
import com.churninsight.backend_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class PredictionServiceImpl implements PredictionService{
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    @Value("${api.ds.url}")
    private String dsServiceUrl;

    public PredictionServiceImpl(CustomerRepository customerRepository, RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public PredictionResponse processPrediction(PredictionRequest request) {
        // 1. Llamar al microservicio Python para obtener la predicción real
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
        // Guardamos si el modelo predijo Churn o no
        customer.setChurned(response != null && "Churn".equalsIgnoreCase(response.getPrediction()));
        customerRepository.save(customer);

        // 3. Retornar la respuesta del microservicio Python
        return response;
    }
}
