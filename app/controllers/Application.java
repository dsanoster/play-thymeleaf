package controllers;

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
    form.
    return index(form);
  }

  public Result index(Form<UserForm> form) {
    return ok(thymeleaf.render("index", form));
  }

  public Result createUser() {
    Form<UserForm> form = Form.form(UserForm.class).bindFromRequest();
    if (form.hasErrors()) {
      index(form);
    }
    return ok(thymeleaf.render("index", form));
  }
}
