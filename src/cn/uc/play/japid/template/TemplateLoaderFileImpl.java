package cn.uc.play.japid.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.InvalidFileNameException;
import org.apache.commons.lang.NullArgumentException;

import play.Logger;
import play.Play;

import cn.uc.play.japid.UJapidPlugin;
import cn.uc.play.japid.exception.TemplateCompileException;
import cn.uc.play.japid.util.FileUtils;

/**
 * Load templates from file system.
 * 
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-29
 */
public class TemplateLoaderFileImpl implements UJapidTemplateLoader {

	private Map<String, UJapidTemplate> templatesCache = new ConcurrentHashMap();

	private File root;

	private Pattern filter;

	private long nativeCacheExpire = 3 * 60 * 1000;

	/**
	 * Constructor.
	 * 
	 * @param templateDir
	 *            The directory of templates storing.
	 * @param filterRegex
	 *            Template source file loader filter regex pattern.
	 * @param nativeCacheExpire
	 *            Template source file native cache expire.
	 * 
	 * @throws IOException
	 * 
	 */
	public TemplateLoaderFileImpl(String templateDir, String filterRegex,
			long nativeCacheExpire) throws IOException {
		if (templateDir == null) {
			throw new NullArgumentException("templateDir");
		}

		this.root = new File(templateDir);

		this.nativeCacheExpire = nativeCacheExpire;

		if (!root.exists()) {
			throw new FileNotFoundException(templateDir);
		}

		filter = filterRegex == null || filterRegex.isEmpty() ? Pattern
				.compile(UJapidTemplateLoader.FILE_FILTER) : Pattern
				.compile(filterRegex);

		loadAllTemplates();
	}

	@Override
	public UJapidTemplate loadTemplate(String path) throws IOException {
		if (path == null || path.isEmpty()) {
			return null;
		}

		Matcher matcher = filter.matcher(path);
		if (!matcher.find()) {
			return null;
		}

		String filePath = root.getPath() + File.separator + path;
		File file = new File(filePath);

		if (!file.exists()) {
			throw new FileNotFoundException(path);
		}

		// Check native cache.
		if (templatesCache.containsKey(path)
				&& templatesCache.get(path).lastModifyTime.getTime() >= file
						.lastModified()) {
			return templatesCache.get(path);

		}

		Date lastModifyTime = new Date(file.lastModified());

		UJapidTemplate template = new UJapidTemplate();
		template.mode = TemplateStoreMode.File;
		template.lastModifyTime = lastModifyTime;
		template.lastSyncTime = new Date();
		template.name = FileUtils.getFileNameInPath(path);
		template.nameWithPath = path;
		template.source = FileUtils.readToEnd(filePath);

		try {
			UJapidTemplate.compileTemplate(template);
			templatesCache.put(path, template);
		} catch (Exception e) {

			template = templatesCache.containsKey(path) ? templatesCache
					.get(path) : null;

			if (template != null) {
				UJapidTemplate.compileTemplate(template);
				template.lastModifyTime = new Date();
				template.lastSyncTime = new Date();
				templatesCache.put(path, template);
				Logger.error(
						e,
						path
								+ " compiling faild. Ignore this compiling request and return the older template object in cache.");
			} else {
				throw new TemplateCompileException(path + " compiling faild.",
						e);
			}

			if (Play.Mode.DEV == Play.mode) {
				throw new TemplateCompileException(path + " compiling faild.",
						e);
			}
		}

		return template;
	}

	@Override
	public Map<String, UJapidTemplate> loadAllTemplates() throws IOException {

		Map<String, UJapidTemplate> map = new HashMap<String, UJapidTemplate>();

		File[] currentLevel = root.listFiles();
		List<File> nextLevelFiles = new ArrayList<File>();

		int currentLevelIndex = 0;
		while (currentLevel != null && currentLevel.length > 0) {
			File file = currentLevel[currentLevelIndex];
			if (file.isFile()) {
				String fileRelativePath = FileUtils.getRelativePath(
						file.getPath(), root.getPath());
				UJapidTemplate t = loadTemplate(fileRelativePath);
				if (t != null) {
					map.put(fileRelativePath, t);
				}
			}

			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				for (File f : subFiles) {
					nextLevelFiles.add(f);
				}

			}

			currentLevelIndex++;

			if (currentLevelIndex == currentLevel.length) {
				currentLevel = nextLevelFiles.toArray(new File[nextLevelFiles
						.size()]);
				currentLevelIndex = 0;
				nextLevelFiles = new ArrayList<File>();
			}
		}

		return map;
	}

	@Override
	public UJapidTemplate getTemplate(String path) throws IOException {
		if (!templatesCache.containsKey(path)) {
			UJapidTemplate template = loadTemplate(path);
			return template;
		}

		UJapidTemplate cachedTemplate = templatesCache.get(path);

		long interval = new Date().getTime()
				- cachedTemplate.lastSyncTime.getTime();

		if (interval > nativeCacheExpire) {
			cachedTemplate = loadTemplate(path);
		}

		return cachedTemplate;
	}

	

}
