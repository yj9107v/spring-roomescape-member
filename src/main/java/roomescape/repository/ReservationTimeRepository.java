package roomescape.repository;

import java.util.List;
import java.util.Optional;
import roomescape.common.exception.ResourceNotFoundException;
import roomescape.domain.ReservationTime;

public interface ReservationTimeRepository {

    List<ReservationTime> findAll();

    Optional<ReservationTime> findById(Long id);

    Long save(ReservationTime time);

    void deleteById(Long id);

    boolean existsById(Long id);

    default ReservationTime getById(Long id, String message) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException(message));
    }
}
