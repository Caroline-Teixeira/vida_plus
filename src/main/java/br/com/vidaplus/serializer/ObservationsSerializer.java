package br.com.vidaplus.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ObservationsSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String observations, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        List<Map<String, String>> observationsList = new ArrayList<>();
        if (observations != null && !observations.trim().isEmpty()) {
            String[] entries = observations.split(" \\|\\| ");
            for (String entry : entries) {
                String[] parts = entry.split(" \\| ");
                if (parts.length >= 2) {
                    String dateTime = parts[0].trim();
                    String text = parts[1].trim();
                    Map<String, String> observationMap = new HashMap<>();
                    observationMap.put("dateTime", dateTime);
                    observationMap.put("text", text);
                    observationsList.add(observationMap);
                }
            }
        }
        gen.writeObject(observationsList);
    }
}