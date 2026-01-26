package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.dto.StatisticsDTO;
import com.churninsight_dev.backend_api.repository.PredictionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/predictions/history")
@CrossOrigin(origins = "http://localhost:5173")
public class PredictionHistoryController {
    private final PredictionRepository predictionRepository;

    public PredictionHistoryController(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @GetMapping
    public List<PredictionHistoryDTO> getPredictionHistory() {
        // Obtener el usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        // Recuperar el historial de predicciones del usuario desde la base de datos
        List<PredictionHistoryDTO> history = predictionRepository.findPredictionHistoryByUserEmail(email);
        
        // Procesar cada predicción para agregar mensajes personalizados si es churn
        for (PredictionHistoryDTO dto : history) {
            // Solo mostrar mensaje si resultado es Churn
            if (dto.getResultado() != null && dto.getResultado().equalsIgnoreCase("churn")) {
                // Generar mensaje personalizado según el factor principal
                String factor = dto.getFactorPrincipal();
                if (factor != null) {
                    // Limpiar el factor para comparación
                    String cleanFactor = factor.replace("_", "").replace("-", "").toLowerCase();
                    String rawFactor = factor.toLowerCase();
                    
                    // Usar switch para determinar el mensaje basado en el factor
                    String msg = switch (cleanFactor) {
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
                            // Manejar casos con prefijos num__ o cat__
                            yield switch (rawFactor) {
                                case "num__monthly_fee" -> "Alerta: Considera ofrecer un descuento del 15% en la tarifa mensual para retener a este cliente.";
                                case "num__last_login_days" -> "Alerta: El cliente lleva varios días sin iniciar sesión. Envía una campaña de reactivación o recordatorio.";
                                case "num__watch_hours" -> "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                                case "num__avg_watch_time_per_day" -> "Alerta: El tiempo de visualización ha disminuido. Recomienda contenido personalizado para aumentar el compromiso.";
                                case "cat__subscription_type" -> "Alerta: Revisa si el plan actual se ajusta a las necesidades del cliente. Sugiere pasar a un plan más completo o más básico según su uso.";
                                case "cat__payment_method" -> "Alerta: El método de pago podría estar generando fricción. Ofrece alternativas más cómodas o incentivos por cambiar.";
                                case "num__number_of_profiles" -> "Alerta: El cliente tiene pocos perfiles activos. Sugiere compartir el servicio con familiares o amigos.";
                                case "cat__favorite_genre" -> "Alerta: Recomienda nuevos títulos del género favorito para aumentar la satisfacción y retención.";
                                default -> null;
                            };
                        }
                    };
                    dto.setCustomMessage(msg);
                } else {
                    dto.setCustomMessage(null);
                }
            } else {
                dto.setCustomMessage(null);
            }
        }
        return history;
    }

    @GetMapping("/statistics")
    public StatisticsDTO getStatistics(
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String subscriptionType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) String gender) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Imprimir información de depuración sobre la solicitud
        System.out.println("=== Advanced Statistics Request ===");
        System.out.println("User: " + email);
        System.out.println("Filters - Age: " + age + ", SubscriptionType: " + subscriptionType +
                          ", Region: " + region + ", Device: " + device + ", Gender: " + gender);

        // Contar el total de predicciones con los filtros aplicados
        Long total = predictionRepository.countTotalByFilters(email, age, subscriptionType, region, device, gender);
        // Contar las predicciones de churn con los filtros aplicados
        Long churn = predictionRepository.countChurnByFilters(email, age, subscriptionType, region, device, gender);
        // Calcular no churn como total menos churn
        Long noChurn = total - churn;
        // Calcular la tasa de churn como porcentaje
        Double churnRate = total > 0 ? (double) churn / total * 100 : 0.0;

        // Imprimir resultados de depuración
        System.out.println("Results - Total: " + total + ", Churn: " + churn + ", NoChurn: " + noChurn + ", Rate: " + churnRate);

        // Retornar el objeto DTO con las estadísticas calculadas
        return new StatisticsDTO(total, churn, noChurn, churnRate);
    }
}
