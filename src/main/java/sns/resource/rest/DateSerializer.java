package sns.resource.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateSerializer
  extends JsonSerializer<Date> {
	private static SimpleDateFormat formatter
      = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss.SSS");


	@Override
	public void serialize(Date date, JsonGenerator generator, SerializerProvider sp)
			throws IOException, JsonProcessingException {
		generator.writeString(formatter.format(date));
	}}