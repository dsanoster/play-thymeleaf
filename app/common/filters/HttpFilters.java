package common.filters;

import play.api.mvc.EssentialFilter;

public class HttpFilters implements play.http.HttpFilters {

  @Override
  public EssentialFilter[] filters() {
    return new EssentialFilter[] {new TestLoggingFilter()};
  }
}
