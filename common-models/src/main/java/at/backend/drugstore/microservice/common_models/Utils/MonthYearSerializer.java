package at.backend.drugstore.microservice.common_models.Utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MonthYearSerializer extends StdSerializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

    public MonthYearSerializer() {
        this(null);
    }

    public MonthYearSerializer(Class<LocalDate> t) {
        super(t);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.format(formatter));
    }
}
