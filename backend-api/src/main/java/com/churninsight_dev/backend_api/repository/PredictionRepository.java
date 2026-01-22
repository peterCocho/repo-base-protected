package com.churninsight_dev.backend_api.repository;

import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.model.Prediction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PredictionRepository extends CrudRepository<Prediction, Long> {
    @Query("SELECT new com.churninsight_dev.backend_api.dto.PredictionHistoryDTO(" +
    "c.customerId, p.resultado, p.probabilidad, p.factorPrincipal, p.monthlyFee, p.fechaPrediccion) " +
    "FROM Prediction p " +
    "LEFT JOIN p.customer c " +
    "LEFT JOIN c.user u " +
    "WHERE u.email = :email " +
    "ORDER BY p.fechaPrediccion DESC")
List<PredictionHistoryDTO> findPredictionHistoryByUserEmail(String email);

    @Query("SELECT new com.churninsight_dev.backend_api.dto.PredictionHistoryDTO(" +
    "c.customerId, p.resultado, p.probabilidad, p.factorPrincipal, p.monthlyFee, p.fechaPrediccion) " +
    "FROM Prediction p " +
    "LEFT JOIN p.customer c " +
    "LEFT JOIN c.user u " +
    "WHERE u.email = :email " +
    "AND (:age IS NULL OR c.age = :age) " +
    "AND (:subscriptionType IS NULL OR c.subscriptionType = :subscriptionType) " +
    "AND (:region IS NULL OR c.region = :region) " +
    "AND (:device IS NULL OR c.device = :device) " +
    "AND (:gender IS NULL OR c.gender = :gender) " +
    "ORDER BY p.fechaPrediccion DESC")
List<PredictionHistoryDTO> findFilteredPredictionHistoryByUserEmail(String email, Integer age, String subscriptionType, String region, String device, String gender);

    @Query("SELECT new com.churninsight_dev.backend_api.dto.PredictionHistoryDTO(" +
    "c.customerId, p.resultado, p.probabilidad, p.factorPrincipal, p.monthlyFee, p.fechaPrediccion) " +
    "FROM Prediction p " +
    "LEFT JOIN p.customer c " +
    "LEFT JOIN c.user u " +
    "ORDER BY p.fechaPrediccion DESC")
List<PredictionHistoryDTO> findPredictionHistory();

    boolean existsByCustomer_CustomerId(String customerId);

    boolean existsByCustomer_User_IdAndCustomer_CustomerId(Long userId, String customerId);

    boolean existsByCustomer_User_CompanyNameAndCustomer_CustomerId(String companyName, String customerId);

    @Query("SELECT COUNT(p) FROM Prediction p LEFT JOIN p.customer c LEFT JOIN c.user u WHERE u.email = :email AND (:age IS NULL OR c.age = :age) AND (:subscriptionType IS NULL OR c.subscriptionType = :subscriptionType) AND (:region IS NULL OR c.region = :region) AND (:device IS NULL OR c.device = :device) AND (:gender IS NULL OR c.gender = :gender)")
    Long countTotalByFilters(String email, Integer age, String subscriptionType, String region, String device, String gender);

    @Query("SELECT COUNT(p) FROM Prediction p LEFT JOIN p.customer c LEFT JOIN c.user u WHERE u.email = :email AND (:age IS NULL OR c.age = :age) AND (:subscriptionType IS NULL OR c.subscriptionType = :subscriptionType) AND (:region IS NULL OR c.region = :region) AND (:device IS NULL OR c.device = :device) AND (:gender IS NULL OR c.gender = :gender) AND p.resultado = 'Churn'")
    Long countChurnByFilters(String email, Integer age, String subscriptionType, String region, String device, String gender);

    @Query("SELECT DISTINCT c.gender FROM Prediction p LEFT JOIN p.customer c LEFT JOIN c.user u WHERE u.email = :email AND c.gender IS NOT NULL")
    List<String> findUniqueGendersByUserEmail(String email);
}
