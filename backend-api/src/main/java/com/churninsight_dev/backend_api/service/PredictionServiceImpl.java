package com.churninsight_dev.backend_api.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;
import com.churninsight_dev.backend_api.model.Customer;
import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.model.Prediction;
import com.churninsight_dev.backend_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.churninsight_dev.backend_api.repository.PredictionRepository;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.security.JwtUtil;


@Service
public class PredictionServiceImpl implements PredictionService {
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${api.ds.url}")
    private String dsServiceUrl;

    public PredictionServiceImpl(CustomerRepository customerRepository,
            PredictionRepository predictionRepository,
            UserRepository userRepository,
            JwtUtil jwtUtil,
            RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    @Override
    public PredictionResponse processPrediction(PredictionRequest request, String jwtToken) {
        // 1. Llamar al microservicio Python para obtener la predicción real
        if (dsServiceUrl == null) {
            throw new IllegalStateException("Data Science service URL (dsServiceUrl) must not be null");
        }
        PredictionResponse response = restTemplate.postForObject(dsServiceUrl, request, PredictionResponse.class);

        // Set customerId in response
        if (response != null) {
            response.setCustomerId(request.getCustomerId());
        }

        // 2. CREAR Y GUARDAR EL CLIENTE EN LA BASE DE DATOS (Persistencia)
        Customer customer = new Customer();
        customer.setCustomerId(request.getCustomerId());
        customer.setAge(request.getAge());
        customer.setWatchHours(request.getWatchHours());
        customer.setLastLoginDays(request.getLastLoginDays());
        customer.setMonthlyFee(request.getMonthlyFee());
        customer.setNumberOfProfiles(request.getNumberOfProfiles());
        customer.setAvgWatchTimePerDay(request.getAvgWatchTimePerDay());
        customer.setSubscriptionType(request.getSubscriptionType());
        customer.setRegion(request.getRegion());
        customer.setDevice(request.getDevice());
        customer.setPaymentMethod(request.getPaymentMethod());
        customer.setFavoriteGenre(request.getFavoriteGenre());
        customer.setGender(request.getGender());

        // Asociar usuario autenticado y log para depuración
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            String token = jwtToken.substring(7);
            String email = jwtUtil.extractUsername(token);
            System.out.println("[DEBUG] Email extraído del token: " + email);
            if (email != null) {
                userRepository.findByEmail(email).ifPresentOrElse(user -> {
                    System.out.println("[DEBUG] Usuario encontrado: " + user.getEmail() + ", companyName: "
                            + user.getCompanyName());
                    customer.setUser(user);
                }, () -> {
                    System.out.println("[DEBUG] Usuario NO encontrado para el email: " + email);
                });
            } else {
                System.out.println("[DEBUG] Email extraído del token es null");
            }
        } else {
            System.out.println("[DEBUG] jwtToken es null o no empieza con 'Bearer '");
        }

        Customer savedCustomer = customerRepository.save(customer);

        if (response != null) {
            Prediction prediction = new Prediction();
            prediction.setResultado(response.getPrediction());
            prediction.setProbabilidad(response.getProbability());
            prediction.setFactorPrincipal(response.getMainFactor());
            prediction.setMonthlyFee(response.getMonthlyFee());
            prediction.setCustomer(savedCustomer);
            prediction.setFechaPrediccion(java.time.LocalDateTime.now());
            predictionRepository.save(prediction);
        }

        // 4. Agregar mensaje personalizado SOLO si la predicción es "Churn"
        if (response != null && "Churn".equalsIgnoreCase(response.getPrediction())
                && response.getMainFactor() != null) {
            String factor = response.getMainFactor().toLowerCase();
            String msg;
            switch (factor) {
                case "num__age" ->
                    msg = "Alerta: La edad del cliente influye en el riesgo de churn. Considera campañas segmentadas por edad.";
                case "num__watch_hours" ->
                    msg = "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                case "num__last_login_days" ->
                    msg = "Alerta: El cliente lleva varios días sin iniciar sesión. Envía una campaña de reactivación o recordatorio.";
                case "num__monthly_fee" ->
                    msg = "Alerta: Considera ofrecer un descuento del 15% en la tarifa mensual para retener a este cliente.";
                case "num__number_of_profiles" ->
                    msg = "Alerta: El cliente tiene pocos perfiles activos. Sugiere compartir el servicio con familiares o amigos.";
                case "num__avg_watch_time_per_day" ->
                    msg = "Alerta: El promedio de horas diarias ha bajado. Recomienda contenido relevante para aumentar el uso.";
                case "cat__subscription_type" ->
                    msg = "Alerta: El tipo de suscripción influye en el churn. Considera promociones para tipos menos estables.";
                case "cat__region" ->
                    msg = "Alerta: La región del cliente influye en el riesgo. Analiza campañas específicas por zona geográfica.";
                case "cat__device" ->
                    msg = "Alerta: El dispositivo principal puede afectar la experiencia. Considera optimizaciones o recomendaciones específicas.";
                case "cat__payment_method" ->
                    msg = "Alerta: El método de pago influye en la retención. Ofrece alternativas o incentivos para métodos menos estables.";
                case "cat__favorite_genre" ->
                    msg = "Alerta: El género favorito del cliente influye en el churn. Recomienda contenido de ese género para aumentar el engagement.";
                case "cat__gender" ->
                    msg = "Alerta: El género del cliente influye en el riesgo. Considera campañas segmentadas por género.";
                default -> msg = "No hay recomendación personalizada para este caso.";
            }
            response.setCustomMessage(msg);
        } else if (response != null) {
            response.setCustomMessage(null);
        }
        System.out.println("[DEBUG] PredictionResponse a retornar: " + response);
        return response;
    }

    // Procesar predicción masiva desde archivo CSV

    public List<PredictionResponse> processCsvPrediction(MultipartFile file, String jwtToken) {
        User user = null;
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            String token = jwtToken.substring(7);
            String email = jwtUtil.extractUsername(token);
            if (email != null) {
                user = userRepository.findByEmail(email).orElse(null);
            }
        }

        List<PredictionRequest> requests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line = reader.readLine();
            if (line == null) throw new RuntimeException("CSV vacío");
            String[] headers = line.split(",");
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                idx.put(headers[i].trim().toLowerCase(), i);
            }
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                PredictionRequest req = new PredictionRequest();
                req.setCustomerId(cols[getIdx(idx, "customer_id")]);
                req.setAge(parseIntOrNull(cols[getIdx(idx, "age")]));
                req.setGender(getSafe(cols, idx, "gender"));
                req.setSubscriptionType(getSafe(cols, idx, "subscription_type"));
                req.setWatchHours(parseDoubleOrNull(getSafe(cols, idx, "watch_hours")));
                req.setLastLoginDays(parseIntOrNull(getSafe(cols, idx, "last_login_days")));
                req.setRegion(getSafe(cols, idx, "region"));
                req.setDevice(getSafe(cols, idx, "device"));
                req.setMonthlyFee(parseDoubleOrNull(getSafe(cols, idx, "monthly_fee")));
                req.setPaymentMethod(getSafe(cols, idx, "payment_method"));
                req.setNumberOfProfiles(parseIntOrNull(getSafe(cols, idx, "number_of_profiles")));
                req.setAvgWatchTimePerDay(parseDoubleOrNull(getSafe(cols, idx, "avg_watch_time_per_day")));
                req.setFavoriteGenre(getSafe(cols, idx, "favorite_genre"));
                System.out.println("[DEBUG] PredictionRequest generado desde CSV: " + req);
                requests.add(req);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo CSV", e);
        }

        // Validar duplicados en el CSV
        Set<String> customerIds = new HashSet<>();
        for (PredictionRequest req : requests) {
            if (!customerIds.add(req.getCustomerId())) {
                throw new RuntimeException("El CSV contiene IDs de clientes duplicados. Verifica que no haya repetidos.");
            }
        }

        // Validar si ya existen predicciones para estos customer_ids en la misma empresa
        for (PredictionRequest req : requests) {
            if (user != null && predictionRepository.existsByCustomer_User_CompanyNameAndCustomer_CustomerId(user.getCompanyName(), req.getCustomerId())) {
                throw new RuntimeException("Algunos IDs de clientes en el CSV ya tienen predicciones realizadas. Verifica que no estén duplicados o ya procesados.");
            }
        }

        return processBatchPrediction(requests, jwtToken);
    }

    // Devuelve el índice de la columna, lanza excepción si no existe
    private int getIdx(Map<String, Integer> idx, String key) {
        Integer i = idx.get(key.toLowerCase());
        if (i == null) throw new RuntimeException("Columna faltante en CSV: " + key);
        return i;
    }

    // Devuelve el valor seguro de la columna, o null si no existe
    private String getSafe(String[] cols, Map<String, Integer> idx, String key) {
        Integer i = idx.get(key.toLowerCase());
        if (i == null || i >= cols.length) return null;
        return cols[i];

    }

    // Métodos utilitarios para parseo seguro
    private Integer parseIntOrNull(String s) {
        try {
            return (s == null || s.isEmpty()) ? null : Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDoubleOrNull(String s) {
        try {
            return (s == null || s.isEmpty()) ? null : Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    // Implementación requerida por PredictionService para processCsvPrediction(MultipartFile)
    @Override
    public List<PredictionResponse> processCsvPrediction(MultipartFile file) {
        return processCsvPrediction(file, null);
    }

    // Implementación del método processBatchPrediction
    public List<PredictionResponse> processBatchPrediction(List<PredictionRequest> requests, String jwtToken) {
        List<PredictionResponse> responses = new ArrayList<>();
        for (PredictionRequest request : requests) {
            PredictionResponse response = processPrediction(request, jwtToken);
            // Asegura que el mensaje personalizado solo esté para "Churn"
            if (response != null && !"Churn".equalsIgnoreCase(response.getPrediction())) {
                response.setCustomMessage(null);
            }
            responses.add(response);
        }
        return responses;
    }

    // Implementación requerida por PredictionService
    @Override
    public PredictionResponse processPrediction(PredictionRequest request) {
        return processPrediction(request, null);
    }
}
