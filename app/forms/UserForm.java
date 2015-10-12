package forms;

import javax.validation.constraints.Pattern;

public class UserForm {

  @Pattern(message = "user.name.error1", regexp = "^[a-zA-Z]{6,}$")
  public String name;

  @Pattern(message = "user.email.error1",
      regexp = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$")
  public String email;
}
