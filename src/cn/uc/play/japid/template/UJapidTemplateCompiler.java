package cn.uc.play.japid.template;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.data.validation.Validation;
import play.templates.JavaExtensions;

import cn.bran.japid.classmeta.AbstractTemplateClassMetaData;
import cn.bran.japid.compiler.JapidTemplateTransformer;
import cn.bran.play.JapidPlayAdapter;
import cn.bran.play.NoEnhance;
import cn.uc.play.japid.UJapidPlugin;

/**
 * Used for transforming template source to java source code, and compiling the
 * java source code. This class was not Thread-safe.
 * 
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-30
 */
public class UJapidTemplateCompiler {
	private static UJapidTemplateCompiler instance;

	private JapidTemplateTransformer jtt;
	private Map<String, Class<?>> staticImports = new HashMap<String, Class<?>>();
	private Set<String> imports = new HashSet<String>();
	private Map<String, Class<? extends Annotation>> typeAnnotations = new HashMap<String, Class<? extends Annotation>>();

	public void importStatic(Class<?> clz) {
		this.staticImports.put(clz.getName(), clz);
	}

	public void addImport(String imp) {
		this.imports.add(imp);
	}

	public void addImport(Class<?> clz) {
		this.imports.add(clz.getName());
	}

	public void addAnnotation(Class<? extends Annotation> anno) {
		typeAnnotations.put(anno.getName(), anno);
	}

	public UJapidTemplateCompiler(String sourceFolder, String targetFolder) {
		jtt = new JapidTemplateTransformer(sourceFolder, targetFolder);
		jtt.usePlay(true);
		// Japid has a bug. If use false, the render will
		// cause an exception of type converting. So I
		// copied some files of Japid-play for the usePlay
		// dependency.
	}

	public static UJapidTemplateCompiler getInstance() {
		if (instance == null) {
			instance = new UJapidTemplateCompiler(UJapidPlugin.ROOT, null);
		}
		return instance;
	}

	public static UJapidTemplateCompiler getInstance(String sourceFolder,
			String targetFolder) {
		if (instance == null) {
			instance = new UJapidTemplateCompiler(sourceFolder, targetFolder);
		}

		return instance;
	}

	public static UJapidTemplateCompiler getInstance(String sourceFolder) {
		if (instance == null) {
			instance = new UJapidTemplateCompiler(sourceFolder, null);
		}

		return instance;
	}

	/**
	 * Do transform from template source to java code.
	 * 
	 * @param templateRelativePath
	 *            Relative path of template.
	 * 
	 * @throws Exception
	 */
	public void transformToJava(String templateRelativePath) throws Exception {
		jtt.generate(templateRelativePath);
	}

	public void clearAllImports() {
		staticImports.clear();
		imports.clear();
		typeAnnotations.clear();
		AbstractTemplateClassMetaData.clearImports();
	}

	public void resetImportsWithoutDefault() {
		for (Map.Entry<String, Class<?>> entry : staticImports.entrySet()) {
			jtt.addImportStatic(entry.getValue());
		}

		for (String importLine : imports) {
			jtt.addImportLine(importLine);
		}

		for (Map.Entry<String, Class<? extends Annotation>> entry : typeAnnotations
				.entrySet()) {
			jtt.addAnnotation(entry.getValue());
		}
	}

	public void resetImports() {
		importStatic(JapidPlayAdapter.class);
		importStatic(Validation.class);
		importStatic(JavaExtensions.class);
		addAnnotation(NoEnhance.class);
		addImport(UJapidPlugin.JAPIDVIEWS_ROOT + "._layouts.*");
		addImport(UJapidPlugin.JAPIDVIEWS_ROOT + "._javatags.*");
		addImport(UJapidPlugin.JAPIDVIEWS_ROOT + "._tags.*");
		addImport("models.*");
		addImport("controllers.*");
		addImport(UJapidPlugin.JAPIDVIEWS_ROOT + "._layouts.*");
		addImport(UJapidPlugin.JAPIDVIEWS_ROOT + "._javatags.*");
		addImport(UJapidPlugin.JAPIDVIEWS_ROOT + "._tags.*");
		addImport("models.*");
		addImport("controllers.*");
		addImport(play.mvc.Scope.class.getName() + ".*");
		addImport(play.i18n.Messages.class);
		addImport(play.i18n.Lang.class);
		addImport(play.mvc.Http.class.getName() + ".*");
		addImport(Validation.class.getName());
		addImport(play.data.validation.Error.class.getName());

		List<String> javatags = scanJavaTags(UJapidPlugin.ROOT);
		for (String f : javatags) {
			addImport("static " + f + ".*");
		}
		
		resetImportsWithoutDefault();
	}
	
	private List<String> scanJavaTags(String root) {
		String sep = File.separator;
		String japidViews = root + sep + UJapidPlugin.JAPIDVIEWS_ROOT + sep;
		File javatags = new File(japidViews + UJapidPlugin.JAVATAGS);
		if (!javatags.exists()) {
			boolean mkdirs = javatags.mkdirs();
			assert mkdirs == true;
		}

		File[] javafiles = javatags.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".java"))
					return true;
				return false;
			}
		});
		
		List<String> files = new ArrayList<String>();
		for (File f : javafiles) {
			String fname = f.getName();
			files.add(UJapidPlugin.JAPIDVIEWS_ROOT + "." + UJapidPlugin.JAVATAGS + "." + fname.substring(0, fname.lastIndexOf(".java")));
		}
		return files;
	}
}
