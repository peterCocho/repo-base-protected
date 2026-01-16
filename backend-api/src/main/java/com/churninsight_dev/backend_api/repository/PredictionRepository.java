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
    "ORDER BY p.fechaPrediccion DESC")
List<PredictionHistoryDTO> findPredictionHistory();
}
