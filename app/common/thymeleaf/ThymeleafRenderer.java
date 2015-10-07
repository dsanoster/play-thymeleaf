package common.thymeleaf;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import org.thymeleaf.util.Validate;

import play.Configuration;
import play.Logger;
import play.Play;
import play.twirl.api.Html;

import com.google.inject.Singleton;

@Singleton
public class ThymeleafRenderer {

	private TemplateEngine engine;

	public ThymeleafRenderer() {
		Configuration config = Play.application().configuration();
		config = config.getConfig("thymeleaf");
		Validate.notNull(config, "Thymeleaf setting not exist");

		String views = config.getString("views");
		String suffix = config.getString("suffix");
		String mode = config.getString("mode");
		Long ttl = config.getMilliseconds("ttl");
		String encoding = config.getString("encoding");
		String include = config.getString("include");

		// デフォルトの設定値
		views = (views == null ? "views/" : views);
		suffix = (suffix == null ? ".html" : suffix);
		mode = (mode == null ? "LEGACYHTML5" : mode);
		ttl = (ttl == null ? 1 * 60 * 1000 : ttl);
		encoding = (encoding == null ? "UTF-8" : encoding);
		include = (include == null ? "include/*" : include);

		// logs/application.logに設定値をログとして出しておく
		Logger.info(String.format("thymeleaf.views: %s", views));
		Logger.info(String.format("thymeleaf.suffix: %s", suffix));
		Logger.info(String.format("thymeleaf.mode: %s", mode));
		Logger.info(String.format("thymeleaf.ttl: %d", ttl));
		Logger.info(String.format("thymeleaf.encoding: %s", encoding));
		Logger.info(String.format("thymeleaf.include: %s", include));

		// thymeleaf.viewsをURIに変換
		URI uri = null;
		try {
			uri = new URI(views);
		} catch (Exception e) {
		}
		Validate.notNull(uri, "Malformat thymeleaf.views: " + views);

		// URIの形式によってリゾルバを選択
		TemplateResolver resolver = null;
		String scheme = uri.getScheme();
		Logger.debug("uri=【" + uri + "】");
		Logger.debug("scheme=【" + scheme + "】");
		Logger.debug("uri.getPath()=【" + uri.getPath() + "】");

		if (scheme == null) {
			resolver = new ClassLoaderTemplateResolver();
			Logger.debug("======ClassLoaderTemplateResolver");
			resolver.setPrefix(uri.getPath());
		} else if (scheme.equals("file")) {
			resolver = new FileTemplateResolver();
			Logger.debug("======FileTemplateResolver");
			resolver.setPrefix(uri.getPath());
		} else {
			resolver = new UrlTemplateResolver();
			Logger.debug("======UrlTemplateResolver");
			resolver.setPrefix(uri.toASCIIString());
		}

		// リゾルバの共通設定
		resolver.setTemplateMode(mode);
		resolver.setSuffix(suffix);
		resolver.setCacheTTLMs(ttl);
		resolver.setCharacterEncoding(encoding);
		Set<String> nonChacheablePatterns = new HashSet<String>();
		nonChacheablePatterns.add(include);
		resolver.setNonCacheablePatterns(nonChacheablePatterns);

		// テンプレートエンジンの作成
		engine = new TemplateEngine();
		engine.setTemplateResolver(resolver);
		engine.addDialect(new LayoutDialect());
	}

	public void clearTemplateCache() {
		engine.clearTemplateCache();
	}

	public Html render(final String templatePath) {
		return render(templatePath, null, null);
	}

	public Html render(final String templatePath, final Object variable) {
		return render(templatePath, variable, null);
	}

	public Html render(final String templatePath,
			final Map<String, ?> additionalVariables) {
		return render(templatePath, null, additionalVariables);
	}

	public Html render(final String templatePath, final Object variable,
			final Map<String, ?> additionalVariables) {
		Context ctx = new Context();
		if (variable != null) {
			ctx.setVariable("it", variable);
		}
		if (additionalVariables != null && additionalVariables.size() > 0) {
			ctx.setVariables(additionalVariables);
		}
		return render(templatePath, ctx);
	}

	public Html render(final String templatePath, Context ctx) {
		return Html.apply(engine.process(templatePath, ctx));
	}
}
