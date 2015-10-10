package controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import common.thymeleaf.ThymeleafRenderer;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

  @Inject
  ThymeleafRenderer thymeleaf;

  public Result index() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("test", "テスト");
    return ok(thymeleaf.render("index", map));
  }

}
