package common.controllers;

import java.util.Map;

import javax.inject.Inject;

import play.filters.csrf.CSRF.TokenProvider;
import play.filters.csrf.CSRF.TokenProviderProvider;
import play.mvc.Controller;

public class CSRFController extends Controller {

  private static final String KEY_CSRFTOKEN = "csrfToken";

  @Inject
  TokenProviderProvider tokenProviderProvider;

  protected String getToken() {
    TokenProvider tokenProvider = tokenProviderProvider.get();
    String csrfToken = tokenProvider.generateToken();
    session().put(KEY_CSRFTOKEN, csrfToken);
    return csrfToken;
  }

  protected boolean checkCsrf() {
    String csrfToken = session().get(KEY_CSRFTOKEN);
    session().remove(KEY_CSRFTOKEN);
    if (csrfToken == null || csrfToken.length() == 0) {
      return false;
    }
    Map<String, String[]> data = request().body().asMultipartFormData().asFormUrlEncoded();
    for (String key : data.keySet()) {
      if (KEY_CSRFTOKEN.equals(key)) {
        return csrfToken.equals(data.get(key)[0]);
      }
    }
    return false;
  }
}
