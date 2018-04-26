package sns.resource.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomDateDeserializer
  extends JsonDeserializer<Date> {
	private static final long serialVersionUID = -1894830519469520844L;
	private static SimpleDateFormat formatter
      = new SimpleDateFormat("MM.dd.yyyy");
 
    @Override
    public Date deserialize(
      JsonParser jsonparser, DeserializationContext context) 
      throws IOException {
         
        String date = jsonparser.getText();

            try {
				return formatter.parse(date);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}

    }
}