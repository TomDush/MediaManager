package fr.dush.mediamanager.business.modules.impl;

import fr.dush.mediamanager.business.modules.IModulesManager;
import fr.dush.mediamanager.exceptions.ModuleLoadingException;

import javax.inject.Named;
import java.util.Collection;

@Named
public class ModulesManagementImpl implements IModulesManager {

    // TODO Implement ModulesManagementImpl based on Spring

    @Override
    public <T> Collection<T> findModuleByType(Class<? extends T> moduleClass) {
        return null;
    }

    @Override
    public <T> T findModuleById(Class<? extends T> moduleClass, String id) throws ModuleLoadingException {
        return null;
    }

    //	@Inject
    //	private BeanManager beanManager;
    //
    //	@Override
    //	public <T> T findModuleById(Class<? extends T> moduleClass, String id) throws ModuleLoadingException {
    //		final Set<Bean<?>> beans = beanManager.getBeans(moduleClass, newModuleAnnotation(id));
    //
    //		// If bean not found, or too many found...
    //		if (beans.size() != 1) {
    //			final String mess = String.format(
    //					"Expected one and only one module with type <%s> with id <%s>,
    // but found %s : %s. Available Ids are : %s", moduleClass,
    //					id, beans.size(), beans, getAvailableModuleIds(moduleClass));
    //
    //			throw new ModuleLoadingException(mess);
    //		}
    //
    //		// Create instance bean return
    //		return this.beanInstanciator(moduleClass).apply(beans.iterator().next());
    //	}
    //
    //	private <T> List<String> getAvailableModuleIds(Class<? extends T> moduleClass) {
    //		List<String> availablesId = newArrayList();
    //
    //		@SuppressWarnings({ "unchecked", "rawtypes" })
    //		final Set<Bean<T>> allBeans = (Set) BeanProvider.getBeanDefinitions(moduleClass, true, true);
    //
    //		for (Bean<T> b : allBeans) {
    //			final Set<Annotation> qualifiers = b.getQualifiers();
    //			for (Annotation a : qualifiers) {
    //				if (a instanceof Module) {
    //					availablesId.add(((Module) a).id());
    //				}
    //			}
    //		}
    //		return availablesId;
    //	}
    //
    //	@Override
    //	public <T> Collection<T> findModuleByType(Class<? extends T> moduleClass) {
    //		final Set<Bean<?>> beans = beanManager.getBeans(moduleClass, newAny());
    //		return Collections2.transform(beans, this.beanInstanciator(moduleClass));
    //	}
    //
    //	/**
    //	 * Function transforming bean definition to instance.
    //	 *
    //	 * @return
    //	 */
    //	protected <T> Function<Bean<?>, T> beanInstanciator(final Class<? extends T> moduleClass) {
    //		final Function<Bean<?>, T> beanInstanciator = new Function<Bean<?>, T>() {
    //			@Override
    //			@SuppressWarnings("unchecked")
    //			public T apply(Bean<?> bean) {
    //				// return typedBean.create(beanManager.createCreationalContext(typedBean));
    //
    //				// return typedBean.create(null);
    //				return (T) beanManager.getReference(bean, moduleClass, beanManager.createCreationalContext(bean));
    //			}
    //		};
    //		return beanInstanciator;
    //	}
    //
    //	private Annotation newAny() {
    //		return new Any() {
    //
    //			@Override
    //			public Class<? extends Annotation> annotationType() {
    //				return Any.class;
    //			}
    //		};
    //	}
    //
    //	private Annotation newModuleAnnotation(final String id) {
    //		return new Module() {
    //
    //			@Override
    //			public Class<? extends Annotation> annotationType() {
    //				return Module.class;
    //			}
    //
    //			@Override
    //			public String id() {
    //				return id;
    //			}
    //
    //			@Override
    //			@Nonbinding
    //			public String packageName() {
    //				return "";
    //			}
    //
    //			@Override
    //			@Nonbinding
    //			public String name() {
    //				return "";
    //			}
    //
    //			@Override
    //			@Nonbinding
    //			public String description() {
    //				return "";
    //			}
    //		};
    //	}

}
