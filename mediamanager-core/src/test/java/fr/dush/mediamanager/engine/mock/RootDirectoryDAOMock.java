package fr.dush.mediamanager.engine.mock;

import static com.google.common.collect.Lists.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Alternative;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExistsException;

@Alternative
public class RootDirectoryDAOMock implements IRootDirectoryDAO {

	@Override
	public RootDirectory findBySubPath(Path path) {
		return null;
	}

	@Override
	public List<RootDirectory> findUsingPath(Collection<String> paths) {
		return newArrayList();
	}

	@Override
	public void persist(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException {

	}

	@Override
	public void update(RootDirectory rootDirectory) throws RootDirectoryAlreadyExistsException {

	}

	@Override
	public List<RootDirectory> findAll() {
		return newArrayList();
	}



}
