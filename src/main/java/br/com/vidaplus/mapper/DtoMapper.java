package br.com.vidaplus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.com.vidaplus.dto.AppointmentDto;
import br.com.vidaplus.dto.MedicalRecordDto;
import br.com.vidaplus.dto.UserDto;
import br.com.vidaplus.model.Appointment;
import br.com.vidaplus.model.MedicalRecord;
import br.com.vidaplus.model.User;

@Mapper(componentModel= "spring")
public interface DtoMapper {

    DtoMapper INSTANCE = Mappers.getMapper(DtoMapper.class);

    UserDto toUserDto(User user);

    MedicalRecordDto toMedicalRecordDto(MedicalRecord medicalRecord);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "healthProfessional.id", target = "healthProfessionalId")
    AppointmentDto toAppointmentDto(Appointment appointment);

    

}
