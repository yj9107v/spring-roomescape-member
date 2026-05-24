package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import roomescape.common.exception.ResourceNotFoundException;
import roomescape.domain.Reservation;

public interface ReservationRepository {

    List<Reservation> findAll();

    List<Reservation> findByName(String name);

    Optional<Reservation> findById(Long id);

    Set<Long> findReservedTimeIdsByDateAndThemeId(LocalDate date, Long themeId);

    boolean existsByTimeId(Long timeId);

    boolean existsByThemeId(Long themeId);

    boolean existsBy(Reservation reservation);

    Long save(Reservation Reservation);

    void deleteById(Long id);

    void updateDateTime(Reservation updated);

    default Reservation getById(Long id, String message) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException(message));
    }
}
