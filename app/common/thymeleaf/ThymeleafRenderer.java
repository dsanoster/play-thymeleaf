package common.thymeleaf;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import com.google.inject.Singleton;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import play.Configuration;
import play.Play;
import play.twirl.api.Html;

@Singleton
public class ThymeleafRenderer {

  private TemplateEngine engine;

  private Configuration getDefaultTemplateResolverConfig() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("prefix", "views/");
    map.put("suffix", ".html");
    map.put("templateMode", "LEGACYHTML5");
    map.put("characterEncoding", "UTF-8");
    // map.put("cacheTTLMs", null);
    map.put("nonCacheablePatterns", new ArrayList<String>());
    map.put("resolvablePatterns", new ArrayList<String>());
    return new Configuration(map);
  }

  private TemplateResolver getTemplateResolver(Configuration conf, Configuration dConf) {
    String prefix = conf.getString("prefix", dConf.getString("prefix"));
    String suffix = conf.getString("suffix", dConf.getString("suffix"));
    String templateMode = conf.getString("templateMode", dConf.getString("templateMode"));
    String characterEncoding =
        conf.getString("characterEncoding", dConf.getString("characterEncoding"));
    Long cacheTTLMs = conf.getLong("cacheTTLMs", dConf.getLong("cacheTTLMs"));
    List<String> nonCacheablePatterns =
        conf.getStringList("nonCacheablePatterns", dConf.getStringList("nonCacheablePatterns"));
    List<String> resolvablePatterns =
        conf.getStringList("resolvablePatterns", dConf.getStringList("resolvablePatterns"));

    TemplateResolver resolver = null;
    URI uri = null;
    try {
      uri = new URI(prefix);
    } catch (Exception e) {
    }

    if (uri == null || uri.getScheme() == null) {
      resolver = new ClassLoaderTemplateResolver();
      resolver.setPrefix(prefix);
    } else if (uri.getScheme().equals("file")) {
      resolver = new FileTemplateResolver();
      resolver.setPrefix(uri.getPath());
    } else {
      resolver = new UrlTemplateResolver();
      resolver.setPrefix(uri.toASCIIString());
    }

    resolver.setSuffix(suffix);
    resolver.setTemplateMode(templateMode);
    resolver.setCharacterEncoding(characterEncoding);
    resolver.setCacheTTLMs(cacheTTLMs);
    resolver.setNonCacheablePatterns(new HashSet<String>(nonCacheablePatterns));
    resolver.setResolvablePatterns(new HashSet<String>(resolvablePatterns));

    return resolver;
  }

  public ThymeleafRenderer() {
    engine = new TemplateEngine();

    Configuration dConf = getDefaultTemplateResolverConfig();
    List<Configuration> confList =
        Play.application().configuration().getConfigList("thymeleafTemplateResolvers");

    if (confList != null && confList.size() > 0) {
      for (int i = 0; i < confList.size(); i++) {
        TemplateResolver resolver = getTemplateResolver(confList.get(i), dConf);
        resolver.setOrder(i + 1);
        engine.addTemplateResolver(resolver);
      }
    } else {
      engine.setTemplateResolver(getTemplateResolver(dConf, dConf));
    }

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

  public Html render(final String templatePath, final Map<String, ?> additionalVariables) {
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
