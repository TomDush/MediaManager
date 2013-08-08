package fr.dush.mediamanager.engine.mock;

import java.nio.file.Path;

import javax.enterprise.inject.Alternative;

import fr.dush.mediamanager.dao.mediatech.IRootDirectoryDAO;
import fr.dush.mediamanager.dto.tree.RootDirectory;
import fr.dush.mediamanager.exceptions.RootDirectoryAlreadyExists;

@Alternative
public class RootDirectoryDAOMock implements IRootDirectoryDAO {

	@Override
	public RootDirectory findRootDirectory(Path path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(RootDirectory rootDirectory) throws RootDirectoryAlreadyExists {
		// TODO Auto-generated method stub

	}

}
