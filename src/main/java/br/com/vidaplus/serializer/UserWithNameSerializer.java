package br.com.vidaplus.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.com.vidaplus.model.User;

public class UserWithNameSerializer extends StdSerializer<User> {

    public UserWithNameSerializer() {
        this(null);
    }

    public UserWithNameSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (user != null) {
            jsonGenerator.writeNumberField("id", user.getId());
            jsonGenerator.writeStringField("name", user.getName());
        }
        jsonGenerator.writeEndObject();
    }
}