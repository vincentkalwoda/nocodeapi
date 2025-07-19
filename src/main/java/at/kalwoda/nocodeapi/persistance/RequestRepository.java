package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Request;
import at.kalwoda.nocodeapi.service.dtos.request.RequestStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, ApiKey> {
    Optional<Request> findByApiKey(ApiKey apiKey);

    List<Request> findByProject_ApiKey(ApiKey projectApiKey);

    @Query("""
                SELECT new at.kalwoda.nocodeapi.service.dtos.request.RequestStatsDto(
                    COUNT(r),
                    AVG(r.responseTime),
                    (SUM(CASE WHEN r.statusCode BETWEEN 200 AND 299 THEN 1 ELSE 0 END) * 1.0 / COUNT(r)),
                    1.0
                )
                FROM Request r
                WHERE r.project.apiKey = :apiKey
            """)
    RequestStatsDto getRequestStats(ApiKey apiKey);


}
