package br.com.vidaplus.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.vidaplus.model.User;

// pra enxugar lista de dados no POSTMAN (id)

public class UserIdSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (user != null) {
            jsonGenerator.writeNumber(user.getId());
        } else {
            jsonGenerator.writeNull();
        }
    }
}