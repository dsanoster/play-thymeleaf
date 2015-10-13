package controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import common.thymeleaf.ThymeleafRenderer;
import forms.UserForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

  @Inject
  ThymeleafRenderer thymeleaf;

  public Result index() {
    Form<UserForm> form = Form.form(UserForm.class).fill(new UserForm());
    return index(form);
  }

  public Result index(Form<UserForm> form) {
    return ok(thymeleaf.render("index", form));
  }

  public Result createUser() {
    Form<UserForm> form = Form.form(UserForm.class).bindFromRequest();
    if (form.hasErrors()) {
      System.out.println(form.get());
      form = Form.form(UserForm.class).bind(new HashMap<String, String>());
      return index(form);
    }
    UserForm userForm = form.get();
    return redirect(routes.Application.getUser(userForm.getName(), userForm.getEmail()));
  }

  public Result getUser(String name, String email) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("name", name);
    map.put("email", email);
    return ok(thymeleaf.render("user", map));
  }
}
