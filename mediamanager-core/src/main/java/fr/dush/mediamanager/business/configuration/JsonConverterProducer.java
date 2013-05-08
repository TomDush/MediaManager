package fr.dush.mediamanager.business.configuration;

import javax.enterprise.inject.Produces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonConverterProducer {

	@Produces
	private ObjectMapper produceObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		return mapper;
	}
}
