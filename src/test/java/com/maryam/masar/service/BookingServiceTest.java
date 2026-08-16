package com.maryam.masar.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.assertj.core.api.Assertions.assertThat;

class BookingServiceTest {

    // We only need calculateRefundPercentage for these tests, which is a pure
    // method with no dependencies — so we can new up BookingService with mocked
    // repositories it never touches for this particular method.
    private final BookingService bookingService = new BookingService(
            Mockito.mock(com.maryam.masar.repository.BookingRepository.class),
            Mockito.mock(com.maryam.masar.repository.TripRepository.class),
            Mockito.mock(com.maryam.masar.repository.PassengerRepository.class),
            Mockito.mock(com.maryam.masar.repository.TicketRepository.class),
            Mockito.mock(com.maryam.masar.repository.WalletTransactionRepository.class),
            Mockito.mock(com.maryam.masar.repository.OperatorRepository.class)
    );

    @Test
    void refund_wellOver24Hours_returns100Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusHours(48);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void refund_exactly24Hours_returns100Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusHours(24);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void refund_justUnder24Hours_returns50Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusHours(24).minusMinutes(1);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(new BigDecimal("0.5"));
    }

    @Test
    void refund_wellWithin24HourWindow_returns50Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusHours(10);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(new BigDecimal("0.5"));
    }

    @Test
    void refund_exactly2Hours_returns50Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusHours(2);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(new BigDecimal("0.5"));
    }

    @Test
    void refund_justUnder2Hours_returns0Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusHours(2).minusMinutes(1);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void refund_wellUnder2Hours_returns0Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T00:00:00+03:00");
        OffsetDateTime departure = now.plusMinutes(30);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void refund_afterDeparture_returns0Percent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-08-16T12:00:00+03:00");
        OffsetDateTime departure = now.minusHours(1);

        BigDecimal result = bookingService.calculateRefundPercentage(now, departure);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }
}