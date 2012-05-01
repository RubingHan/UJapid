package cn.uc.play.japid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.uc.play.japid.exception.TemplateEngineInitException;
import cn.uc.play.japid.template.UJapidTemplate;
import cn.uc.play.japid.template.UJapidTemplateLoader;
import cn.uc.play.japid.template.UJapidTemplateLoaderFactory;
import cn.uc.play.japid.util.FileUtils;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.classloading.ApplicationClasses;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

/**
 * Plugin for Japid in play framework. Implement loading template from db, and
 * dynamic compiling.
 * 
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-29
 */
public class UJapidPlugin extends PlayPlugin {

	public static final String JAPIDVIEWS_ROOT = "japidviews";
	public static final String JAVATAGS = "_javatags";
	public static final String LAYOUTDIR = "_layouts";
	public static final String TAGSDIR = "_tags";

	public static final String ROOT = "app";

	public static UJapidTemplateLoader templateLoader;

	@Override
	public void onLoad() {
		Logger.info("UJapidPlugin starting...");
		Logger.info("Clear java source files of UJapid.");
		try {
			FileUtils.clearFiles(ROOT + File.separator + JAPIDVIEWS_ROOT,
					"^.*\\.java$", true);
		} catch (FileNotFoundException e) {
			Logger.error(e, "Clear java files faild.");
		}
	}

	@Override
	public void beforeDetectingChanges() {
	}
	
	

	@Override
	public void onApplicationStart() {
		try {
			if (templateLoader == null) {
				templateLoader = UJapidTemplateLoaderFactory.create();
				templateLoader.loadAllTemplates();
			}

		} catch (Exception e) {
			throw new TemplateEngineInitException(e);
		}

	}

	@Override
	public boolean rawInvocation(Request request, Response response)
			throws Exception {
		return super.rawInvocation(request, response);
	}

}
