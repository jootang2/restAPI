package me.jootang2.inflearn2.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("errors");
        jsonGenerator.writeStartArray();
        for(FieldError e : errors.getFieldErrors()){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("field", e.getField());
            jsonGenerator.writeStringField("objectName", e.getObjectName());
            jsonGenerator.writeStringField("code", e.getCode());
            jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
            Object rejectedValue = e.getRejectedValue();
            if(rejectedValue != null){
                jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
            }
            jsonGenerator.writeEndObject();
        }

        for(ObjectError e : errors.getGlobalErrors()){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("objectName", e.getObjectName());
            jsonGenerator.writeStringField("code", e.getCode());
            jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }
}
