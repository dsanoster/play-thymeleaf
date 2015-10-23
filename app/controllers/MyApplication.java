package controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import common.controllers.CSRFController;
import common.thymeleaf.ThymeleafRenderer;
import forms.UserForm;
import play.data.Form;
import play.mvc.Result;

public class MyApplication extends CSRFController {

  @Inject
  ThymeleafRenderer thymeleaf;

  public Result index() {
    Form<UserForm> form = Form.form(UserForm.class).fill(new UserForm());
    return index(form);
  }

  public Result index(Form<UserForm> form) {
    String csrfToken = getToken();
    form.data().put("csrfToken", csrfToken);
    return ok(thymeleaf.render("index", form));
  }

  public Result createUser() {
    Form<UserForm> form = Form.form(UserForm.class).bindFromRequest();
    if (!checkCsrf(form.data().get("csrfToken"))) {
      return forbidden("CSRF Error has occurred.");
    }
    if (form.hasErrors()) {
      return index(form);
    }
    UserForm userForm = form.get();
    return redirect(routes.MyApplication.getUser(userForm.getName(), userForm.getEmail()));
  }

  public Result getUser(String name, String email) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("name", name);
    map.put("email", email);
    return ok(thymeleaf.render("user", map));
  }

  public Result clearTemplateCache() {
    thymeleaf.clearTemplateCache();
    return ok("Template cache has cleared.");
  }
}
