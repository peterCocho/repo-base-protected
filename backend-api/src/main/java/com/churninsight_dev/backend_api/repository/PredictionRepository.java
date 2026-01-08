package com.churninsight_dev.backend_api.repository;

import com.churninsight_dev.backend_api.dto.PredictionHistoryDTO;
import com.churninsight_dev.backend_api.model.Prediction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PredictionRepository extends CrudRepository<Prediction, Long> {
        @Query("SELECT new com.churninsight_dev.backend_api.dto.PredictionHistoryDTO(" +
            "u.companyName, c.customerId, c.region, p.resultado, p.probabilidad, p.factorPrincipal) " +
            "FROM Prediction p " +
            "JOIN p.customer c " +
            "JOIN c.user u")
        List<PredictionHistoryDTO> findPredictionHistory();
}
