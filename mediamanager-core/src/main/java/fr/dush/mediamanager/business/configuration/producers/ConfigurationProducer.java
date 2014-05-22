package fr.dush.mediamanager.business.configuration.producers;

import fr.dush.mediamanager.business.configuration.ModuleConfiguration;

/**
 * Provide configuration on all application, implementation must be a {@link ModuleConfiguration} producer !
 * <p/>
 * <p> Configuration values are found (first override next ones) : <ol> <li>Database persisted values</li>
 * <li>(Configuration file)</li> <li>Default properties file in classpath</li> <li>Values given in code</li> </ol> </p>
 * <p/>
 * <p> This is NOT an alternative. It's this only alternative to force using producer. </p>
 *
 * @author Thomas Duchatelle
 */
public class ConfigurationProducer {

    // TODO Implement ConfigurationProducer with Spring

    //	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProducer.class);
    //
    //	/** File configuration DAO : don't need database connection */
    //	@Inject
    //	@FileConfigurationDAO
    //	private IConfigurationDAO configurationDAO;
    //
    //	@Inject
    //	private ObjectMapper objectMapper;
    //
    //	@Inject
    //	private IConfigurationRegister configurationRegister;
    //
    //	/** Configuration generic for all application : it doesn't depend on any module */
    //	private ModuleConfiguration generic;
    //
    //
    //	/**
    //	 * Instanciate generic module
    //	 *
    //	 * @throws IOException
    //	 */
    //	@PostConstruct
    //	public void generateGenericModule() {
    //		try {
    //			generic = getModuleConfiguration("generic", "Media Manager Configuration",
    // "configuration/connection.json");
    //		} catch (IOException e) {
    //			throw new ConfigurationException(e.getMessage(), e);
    //		}
    //	}
    //
    //	public ModuleConfiguration getModuleConfiguration(String configId, String configurationName,
    // String definition) throws IOException {
    //		return createConfiguration(configurationDAO, new SimpleConfigurationArguments(configId,
    // configurationName, definition));
    //	}
    //
    //	/**
    //	 * Produce appropriate {@link ModuleConfiguration} to class which need id, by injection. Values are override
    // by those in database.
    //	 *
    //	 * @param point Point where inject module.
    //	 * @param configurationDAO Default configurationDAO : need database initialised.
    //	 * @return
    //	 */
    //	@Produces
    //	public ModuleConfiguration findModuleConfiguration(InjectionPoint point, IConfigurationDAO configurationDAO) {
    //		LOGGER.debug("Create/find ModuleConfiguration for {}.{}", point.getBean().getBeanClass().getName(),
    // point.getMember().getName());
    //
    //		try {
    //			// Find or create ModuleConfiguration
    //			return createConfiguration(configurationDAO, new ConfigurationInjectionPoint(point));
    //
    //		} catch (IOException e) {
    //			throw new ConfigurationException(ConfigurationInjectionPoint.generateErrorString(point,
    // e.getMessage()));
    //		}
    //	}
    //
    //	/**
    //	 * Same method than {@link #findModuleConfiguration(InjectionPoint, IConfigurationDAO)},
    // but without Database DAO.
    //	 *
    //	 * @param point
    //	 * @return
    //	 */
    //	@Produces
    //	@ConfigurationWithoutDatabase
    //	public ModuleConfiguration findModuleConfiguration(InjectionPoint point) {
    //        LOGGER.warn("Point: {}", point);
    //        LOGGER.warn("Point.bean: {}", point.getBean());
    //        LOGGER.warn("Point.getAnnotated: {}", point.getAnnotated());
    //        LOGGER.warn("Point.getMember: {}", point.getMember());
    //        LOGGER.warn("Point.getQualifiers: {}", point.getQualifiers());
    //        LOGGER.warn("Point.getType: {}", point.getType());
    //        if (point.getBean() == null) {
    //            return null;
    //        }
    //        LOGGER.warn("Point.bean.beanClass: {}", point.getBean().getBeanClass());
    //        LOGGER.debug("Create/find StaticModuleConfiguration for {}.{}",
    //                     point.getBean().getBeanClass().getName(),
    //                     point.getMember().getName());
    //
    //        try {
    //            // Find or create ModuleConfiguration
    //            return createConfiguration(configurationDAO, new ConfigurationInjectionPoint(point));
    //
    //        } catch (IOException e) {
    //            throw new ConfigurationException(ConfigurationInjectionPoint.generateErrorString(point,
    // e.getMessage()));
    //        }
    //    }
    //
    //	/**
    //	 * Create ModuleConfiguration...
    //	 *
    //	 * @param configurationDAO Configuration DAO to use : by file or by database.
    //	 * @param wrapper Args needed to find configuration
    //	 * @return
    //	 * @throws IOException
    //	 */
    //	protected ModuleConfiguration createConfiguration(IConfigurationDAO configurationDAO,
    // final IConfigurationArguments wrapper)
    //			throws IOException {
    //
    //		// Load default value and displayable data...
    //		FieldSet fieldSet = new FieldSet(wrapper.getPackage());
    //		if (isEmpty(fieldSet.getName())) {
    //			fieldSet.setName(wrapper.getName());
    //		}
    //
    //		final String file = wrapper.getDefinition();
    //		if (isNotBlank(file)) {
    //			LOGGER.debug("Initialize {} with {}", wrapper.getPackage(), file);
    //
    //			fieldSet.addAllFields(readClasspathFile(file), true);
    //		}
    //
    //		// Override with user preference
    //		List<Field> fields = configurationDAO.findByPackage(wrapper.getPackage());
    //		for (Field f : fields) {
    //			fieldSet.addValue(f.getKey(), f.getValue(), false);
    //		}
    //
    //		LOGGER.info("Create ModuleConfiguration from {}", fieldSet);
    //		// Create module configuration
    //		return newModuleConfiguration(fieldSet);
    //	}
    //
    //	private ModuleConfiguration newModuleConfiguration(FieldSet fieldSet) {
    //		final ModuleConfiguration config = new ModuleConfiguration(generic, fieldSet);
    //		configurationRegister.registerConfiguration(config);
    //
    //		return config;
    //	}
    //
    //	/**
    //	 * Read and parse to {@link Field}s file in class path.
    //	 */
    //	protected List<Field> readClasspathFile(String file) throws IOException, JsonParseException,
    // JsonMappingException {
    //		// Read file
    //		final InputStream stream = getClass().getClassLoader().getResourceAsStream(file);
    //		if (null == stream) {
    //			throw new IOException(String.format("Class path file %s doesn't exist.", file));
    //		}
    //
    //		// Convert...
    //		final List<Field> fields = objectMapper.readValue(stream, new TypeReference<List<Field>>() {});
    //		return fields;
    //	}
    //
    //	@Getter
    //	@AllArgsConstructor
    //	public class SimpleConfigurationArguments implements IConfigurationArguments {
    //		private String configId;
    //
    //		private String name;
    //
    //		private String definition;
    //
    //		@Override
    //		public String getPackage() {
    //			return getConfigId();
    //		}
    //	}

}
