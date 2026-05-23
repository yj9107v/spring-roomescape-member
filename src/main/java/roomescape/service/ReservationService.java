package roomescape.service;

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.DuplicateReservationException;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationUpdateRequest;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository timeRepository,
            ThemeRepository themeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByName(String name) {
        return reservationRepository.findByName(name);
    }

    public Reservation getReservation(Long id) {
        return reservationRepository.getById(id, "존재하지 않는 예약입니다.");
    }

    @Transactional
    public Reservation addReservation(ReservationRequest request) {
        Reservation reservation = createReservation(
                request,
                getReservationTime(request.timeId()),
                getTheme(request.themeId()));

        reservation.verifyReservable(LocalDateTime.now());
        verifyNoConflict(reservation);

        return getReservation(reservationRepository.save(reservation));
    }

    private ReservationTime getReservationTime(Long timeId) {
        return timeRepository.getById(timeId, "예약할 수 없는 시간입니다.");
    }

    private Theme getTheme(Long themeId) {
        return themeRepository.getById(themeId, "예약할 수 없는 테마입니다.");
    }

    @Nonnull
    private Reservation createReservation(ReservationRequest request, ReservationTime reservationTime,
                                          Theme theme) {
        return new Reservation(
                request.name(),
                request.date(),
                reservationTime,
                theme
        );
    }

    @Transactional
    public void deleteReservation(Long id) {
        getReservation(id);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void cancelMyReservation(Long id, String name) {
        Reservation reservation = getReservation(id);
        reservation.cancelBy(name, LocalDateTime.now());
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation updateReservation(Long id, String name, ReservationUpdateRequest request) {
        Reservation original = getReservation(id);
        Reservation updated = original.changeBy(
                name,
                LocalDateTime.now(),
                request.date(),
                getReservationTime(request.timeId())
        );
        verifyNoConflict(updated);
        reservationRepository.updateDateTime(updated);
        return getReservation(id);
    }

    private void verifyNoConflict(Reservation reservation) {
        if (reservationRepository.existsBy(reservation)) {
            throw new DuplicateReservationException("이미 같은 시점·테마에 예약이 존재합니다.");
        }
    }
}
