package com.churninsight.backend_api.service;

import com.churninsight.backend_api.dto.PredictionRequest;
import com.churninsight.backend_api.dto.PredictionResponse;
import com.churninsight.backend_api.model.Customer;
import com.churninsight.backend_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
import org.springframework.web.client.RestTemplate;
=======
import org.springframework.web.reactive.function.client.WebClient;
>>>>>>> 67093911e194976f80dbd0470f26c36a3f763b06

@Service

public class PredictionServiceImpl implements PredictionService{
    private final CustomerRepository customerRepository;
<<<<<<< HEAD
    private final RestTemplate restTemplate;
    @Value("${api.ds.url}")
    private String dsServiceUrl;

    public PredictionServiceImpl(CustomerRepository customerRepository, RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
=======
    private final WebClient webClient;

    @Value("${api.ds.url}")
    private String dsApiUrl;

    // Inyectamos el repositorio para poder guardar en la DB
    public PredictionServiceImpl(CustomerRepository customerRepository, WebClient webClient) {
        this.customerRepository = customerRepository;
        // Para las escuchas externas http
        this.webClient = webClient;
>>>>>>> 67093911e194976f80dbd0470f26c36a3f763b06
    }

    @Override
    public PredictionResponse processPrediction(PredictionRequest request) {
<<<<<<< HEAD
        // 1. Llamar al microservicio Python para obtener la predicción real
        PredictionResponse response = restTemplate.postForObject(dsServiceUrl, request, PredictionResponse.class);
=======
        // --- PROXIMAMENTE: INTEGRACION REAL ---
        // Descomenta este bloque cuando la API de Python esté lista:
        /*
        PredictionResponse dsResponse = webClient.post()
            .uri(dsApiUrl)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(PredictionResponse.class)
            .block(); // Espera la respuesta de forma sincrónica
        */

        // 1. Simular la lógica del modelo (esto luego será la llamada a Python)
        String prevision = (request.getLastLoginDays() > 20) ? "Va a cancelar" : "Va a continuar";
        double probabilidad = (prevision.equals("Va a cancelar")) ? 0.88 : 0.12;
>>>>>>> 67093911e194976f80dbd0470f26c36a3f763b06

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
