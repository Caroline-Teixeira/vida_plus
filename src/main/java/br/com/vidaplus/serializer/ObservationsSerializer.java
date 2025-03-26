package br.com.vidaplus.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ObservationsSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Substitui "\n---\n" e "\n" por ", " na saída
            String formattedValue = value.replace("\n---\n", " -- ").replace("\n", " -- ");
            gen.writeString(formattedValue);
        }
    }
}