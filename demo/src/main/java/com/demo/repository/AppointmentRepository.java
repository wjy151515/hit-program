package com.demo.repository;

import com.demo.hospital.Appointment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AppointmentRepository {
    private List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> findByDoctorIdAndLastUpdateTimeBetween(int doctorId, long startTime, long endTime) {
        return appointments.stream()
                .filter(appointment -> appointment.getDoctor() == doctorId &&
                        appointment.getLastUpdateTime() > startTime &&
                        appointment.getLastUpdateTime() <= endTime)
                .collect(Collectors.toList());
    }
}
