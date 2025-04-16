package br.com.vidaplus.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ObservationsSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String observations, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (observations == null || observations.trim().isEmpty()) {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeEndArray();
            return;
        }

        jsonGenerator.writeStartArray();
        String[] observationEntries = observations.split(" \\|\\| ");
        for (String entry : observationEntries) {
            String[] parts = entry.split(" \\| ", 3);
            if (parts.length < 3) {
                continue; // Ignora entradas mal formatadas
            }

            String id = parts[0].trim();
            String dateTimeStr = parts[1].trim();
            String observationText = parts[2].trim();

            jsonGenerator.writeStartObject();
            if (id.startsWith("Surgery-")) {
                jsonGenerator.writeStringField("surgeryId", id.replace("Surgery-", ""));
            } else {
                jsonGenerator.writeStringField("appointmentId", id);
            }
            jsonGenerator.writeStringField("dateTime", dateTimeStr);
            jsonGenerator.writeStringField("text", observationText);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}