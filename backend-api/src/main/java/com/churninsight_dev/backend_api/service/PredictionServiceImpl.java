package com.churninsight_dev.backend_api.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;

import com.churninsight_dev.backend_api.dto.PredictionRequest;
import com.churninsight_dev.backend_api.dto.PredictionResponse;
import com.churninsight_dev.backend_api.model.Customer;
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

        // Asociar usuario autenticado y log para depuración
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            String token = jwtToken.substring(7);
            String email = jwtUtil.extractUsername(token);
            System.out.println("[DEBUG] Email extraído del token: " + email);
            if (email != null) {
                userRepository.findByEmail(email).ifPresentOrElse(user -> {
                    System.out.println("[DEBUG] Usuario encontrado: " + user.getEmail() + ", companyName: " + user.getCompanyName());
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
            prediction.setCustomer(savedCustomer);
            predictionRepository.save(prediction);
        }

        // 4. Agregar mensaje personalizado según el mainFactor
        if (response != null && response.getMainFactor() != null) {
            String factor = response.getMainFactor()
                .replace("_", "")    // Elimina guiones bajos
                .replace("-", "")    // Elimina guiones si los hubiera
                .toLowerCase();      // Convierte todo a minúsculas

            String rawFactor = response.getMainFactor().toLowerCase();

            String msg = switch (factor) {
                case "monthlyfee", "monthly_fee" -> "Alerta: Considera ofrecer un descuento del 15% en la tarifa mensual para retener a este cliente.";
                case "lastlogindays", "last_login_days" -> "Alerta: El cliente lleva varios días sin iniciar sesión. Envía una campaña de reactivación o recordatorio.";
                case "watchhours", "watch_hours" -> "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                case "avgwatchtimeperday", "avg_watch_time_per_day" -> "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                case "subscriptiontype", "subscription_type" -> "Alerta: Revisa si el plan actual se ajusta a las necesidades del cliente. Sugiere pasar a un plan más completo o más básico según su uso.";
                case "paymentmethod", "payment_method" -> "Alerta: El método de pago podría estar generando fricción. Ofrece alternativas más cómodas o incentivos por cambiar.";
                case "numberofprofiles", "number_of_profiles" -> "Alerta: El cliente tiene pocos perfiles activos. Sugiere compartir el servicio con familiares o amigos.";
                case "region" -> "Alerta: Personaliza la oferta según la región. Considera promociones locales o contenido relevante.";
                case "device" -> "Alerta: El cliente usa un dispositivo poco frecuente. Ofrece soporte o recomendaciones para mejorar la experiencia.";
                case "favoritegenre", "favorite_genre" -> "Alerta: Recomienda nuevos títulos del género favorito para aumentar la satisfacción y retención.";
                case "age" -> "Alerta: Personaliza la comunicación y las recomendaciones según el perfil demográfico del cliente.";
                case "gender" -> "Alerta: Personaliza la comunicación y las recomendaciones según el perfil demográfico del cliente.";
                default -> {
                    // con el valor original (por si viene con guiones bajos)
                    yield switch (rawFactor) {
                        case "num__monthly_fee" -> "Alerta: Considera ofrecer un descuento del 15% en la tarifa mensual para retener a este cliente.";
                        case "num__last_login_days" -> "Alerta: El cliente lleva varios días sin iniciar sesión. Envía una campaña de reactivación o recordatorio.";
                        case "num__watch_hours" -> "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                        case "num__avg_watch_time_per_day" -> "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                        case "cat__subscription_type" -> "Alerta: Revisa si el plan actual se ajusta a las necesidades del cliente. Sugiere pasar a un plan más completo o más básico según su uso.";
                        case "cat__payment_method" -> "Alerta: El método de pago podría estar generando fricción. Ofrece alternativas más cómodas o incentivos por cambiar.";
                        case "num__number_of_profiles" -> "Alerta: El cliente tiene pocos perfiles activos. Sugiere compartir el servicio con familiares o amigos.";
                        case "cat__favorite_genre" -> "Alerta: Recomienda nuevos títulos del género favorito para aumentar la satisfacción y retención.";
                        default -> "No hay recomendación personalizada para este caso.";
                    };
                }
            };
            response.setCustomMessage(msg);
        }
        return response;
    }


    // Procesar predicción masiva desde archivo CSV

    public List<PredictionResponse> processCsvPrediction(MultipartFile file, String jwtToken) {
        List<PredictionRequest> requests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; } // Saltar encabezado
                String[] cols = line.split(",");
                // Ajusta el orden de los campos según tu CSV
                PredictionRequest req = new PredictionRequest();
                req.setCustomerId(cols[0]);
                req.setAge(Integer.parseInt(cols[1]));
                req.setGender(cols[2]);
                req.setSubscriptionType(cols[3]);
                req.setWatchHours(Double.parseDouble(cols[4]));
                req.setLastLoginDays(Integer.parseInt(cols[5]));
                req.setRegion(cols[6]);
                req.setDevice(cols[7]);
                req.setMonthlyFee(Double.parseDouble(cols[8]));
                req.setPaymentMethod(cols[9]);
                req.setNumberOfProfiles(Integer.parseInt(cols[10]));
                req.setAvgWatchTimePerDay(Double.parseDouble(cols[11]));
                req.setFavoriteGenre(cols[12]);
                requests.add(req);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo CSV", e);
        }
        return processBatchPrediction(requests, jwtToken);
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
            responses.add(processPrediction(request, jwtToken));
        }
        return responses;
    }

    // Implementación requerida por PredictionService
    @Override
    public PredictionResponse processPrediction(PredictionRequest request) {
        return processPrediction(request, null);
    }
}
