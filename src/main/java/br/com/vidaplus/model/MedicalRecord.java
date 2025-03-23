package br.com.vidaplus.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {

    // Atributos
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String observations;

    @OneToMany(mappedBy = "medicalRecord")
    private List<Appointment> appointments = new ArrayList<>();

}
