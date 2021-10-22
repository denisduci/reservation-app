package com.ikub.reservationapp.appointments.utils;

import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StatusUtil {

    public static boolean canOverwriteStatus(Status newStatus, Status oldStatus) {
        boolean canOverwrite = false;
        if (newStatus.equals(oldStatus)) {
            log.error("already in this status");
            throw new ReservationAppException("already in this status");
        }
        switch (newStatus) {
            case PENDING:
                log.error("default status");
                throw new ReservationAppException("default status");
            case APPROVED:
                switch (oldStatus) {
                    case PENDING:
                        log.info("setting from PENDING to APPROVED");
                        canOverwrite = true;
                        break;
                    case DONE:
                        log.error("cannot change from DONE to APPROVED");
                        canOverwrite = false;
                        break;
                    case DOCTOR_CHANGE_APPROVED:
                        log.info("setting from DOCTOR_CHANGE_APPROVED to APPROVED");
                        canOverwrite = true;
                        break;
                    default:
                        log.error("cannot set from this status to APPROVED");
                        canOverwrite = false;
                        break;
                }
                break;
            case DONE:
                switch (oldStatus) {
                    case APPROVED:
                        log.info("setting from APPROVED to DONE");
                        canOverwrite = true;
                        break;
                    default:
                        log.error("cannot set from this status to DONE");
                        canOverwrite = false;
                        break;
                }
                break;
            case CANCELED_BY_PATIENT:
            case CANCELED_BY_DOCTOR:
            case CANCELED_BY_SECRETARY:
                switch (oldStatus) {
                    case PENDING:
                        log.info("setting from PENDING to CANCELED");
                        canOverwrite = true;
                        break;
                    case APPROVED:
                        log.info("setting from APPROVED to CANCELED");
                        canOverwrite = true;
                        break;
                    default:
                        log.error("cannot set from this status to CANCELED");
                        canOverwrite = false;
                        break;
                }
                break;
            case DOCTOR_CHANGE_REQUEST:
                switch (oldStatus) {
                    case PENDING:
                        log.info("from pending to DOCTOR_CHANGE_REQUEST");
                        canOverwrite = true;
                        break;
                    case APPROVED:
                        log.info("from APPROVED to DOCTOR_CHANGE_REQUEST");
                        canOverwrite = true;
                        break;
                    default:
                        canOverwrite = false;
                        break;
                }
                break;
            case DOCTOR_CHANGE_APPROVED:
            case DOCTOR_CHANGE_REFUSED:
                switch (oldStatus) {
                    case DOCTOR_CHANGE_REQUEST:
                        log.info("from DOCTOR_CHANGE_REQUEST to DOCTOR_CHANGE_APPROVED/REFUSED");
                        canOverwrite = true;
                        break;
                    default:
                        canOverwrite = false;
                        break;
                }
                break;
        }
        return canOverwrite;
    }
}
