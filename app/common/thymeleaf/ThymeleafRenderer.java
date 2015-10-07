package common.thymeleaf;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import com.google.inject.Singleton;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import play.Configuration;
import play.Logger;
import play.Play;
import play.twirl.api.Html;

@Singleton
public class ThymeleafRenderer {

  private TemplateEngine engine;

  public ThymeleafRenderer() {
    Configuration config = Play.application().configuration();
    String prefix = config.getString("thymeleaf.prefix", "views/");
    String suffix = config.getString("thymeleaf.suffix", ".html");
    String templateMode = config.getString("thymeleaf.templateMode", "LEGACYHTML5");
    String characterEncoding = config.getString("thymeleaf.characterEncoding", "UTF-8");
    String nonChacheablePattern = config.getString("thymeleaf.nonChacheablePattern", "include/*");
    Long cacheTTLMs = config.getMilliseconds("thymeleaf.cacheTTLMs", null);

    TemplateResolver resolver = null;
    URI uri = null;
    try {
      uri = new URI(prefix);
    } catch (Exception e) {
    }

    if (uri == null || uri.getScheme() == null) {
      resolver = new ClassLoaderTemplateResolver();
      resolver.setPrefix(prefix);

      Logger.debug("======ClassLoaderTemplateResolver");
      Logger.debug("======" + prefix);
    } else if (uri.getScheme().equals("file")) {
      resolver = new FileTemplateResolver();
      resolver.setPrefix(uri.getPath());

      Logger.debug("======FileTemplateResolver");
      Logger.debug("======" + uri.getPath());
    } else {
      resolver = new UrlTemplateResolver();
      resolver.setPrefix(uri.toASCIIString());

      Logger.debug("======UrlTemplateResolver");
      Logger.debug("======" + uri.toASCIIString());
    }

    resolver.setSuffix(suffix);
    resolver.setTemplateMode(templateMode);
    resolver.setCharacterEncoding(characterEncoding);
    Set<String> nonChacheablePatterns = new HashSet<String>();
    nonChacheablePatterns.add(nonChacheablePattern);
    resolver.setNonCacheablePatterns(nonChacheablePatterns);
    resolver.setCacheTTLMs(cacheTTLMs);

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
