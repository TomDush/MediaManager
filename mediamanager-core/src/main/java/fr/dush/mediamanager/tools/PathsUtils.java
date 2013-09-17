package fr.dush.mediamanager.tools;

import static com.google.common.collect.Collections2.*;

import java.nio.file.Paths;
import java.util.Collection;

import com.google.common.base.Function;

public class PathsUtils {

	private static Function<String, String> function = new Function<String, String>() {
		@Override
		public String apply(String s) {
			return Paths.get(s).toAbsolutePath().normalize().toString();
		}
	};

	public static String toAbsolute(String path) {
		return function.apply(path);
	}

	public static Collection<String> toAbsolute(Collection<String> paths) {
		return transform(paths, function);
	}
}
