package com.churninsight.backend_api.service;

import com.churninsight.backend_api.dto.PredictionRequest;
import com.churninsight.backend_api.dto.PredictionResponse;
import com.churninsight.backend_api.model.Customer;
import com.churninsight.backend_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class PredictionServiceImpl implements PredictionService{

    private final CustomerRepository customerRepository;

    // Inyectamos el repositorio para poder guardar en la DB
    public PredictionServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public PredictionResponse processPrediction(PredictionRequest request) {
        // 1. Simular la lógica del modelo (esto luego será la llamada a Python)
        String prevision = (request.getLastLoginDays() > 20) ? "Va a cancelar" : "Va a continuar";
        double probabilidad = (prevision.equals("Va a cancelar")) ? 0.88 : 0.12;

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

        // Guardamos si el modelo predijo Churn o no (opcional, pero útil)
        customer.setChurned(prevision.equals("Va a cancelar"));

        // Guardar físicamente en PostgreSQL
        customerRepository.save(customer);

        // 3. Retornar la respuesta al Controller
        return PredictionResponse.builder()
                .customerId(request.getCustomerId())
                .prevision(prevision)
                .probabilidad(probabilidad)
                .status("Success")
                .build();
    }
}
