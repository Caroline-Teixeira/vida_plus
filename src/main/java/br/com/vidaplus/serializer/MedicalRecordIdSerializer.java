package br.com.vidaplus.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.vidaplus.model.MedicalRecord;

// pra 'enxugar' lista de dados no POSTMAN para prontuarios

public class MedicalRecordIdSerializer extends JsonSerializer<MedicalRecord> {
    @Override
    public void serialize(MedicalRecord medicalRecord, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (medicalRecord != null) {
            jsonGenerator.writeNumber(medicalRecord.getId());
        } else {
            jsonGenerator.writeNull();
        }
    }
}