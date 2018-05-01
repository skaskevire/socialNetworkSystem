package sns.converter;

public interface Converter <I,O>{
	O convert(I input);
}
