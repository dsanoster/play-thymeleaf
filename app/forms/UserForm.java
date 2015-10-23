package forms;

import javax.validation.constraints.Pattern;

public class UserForm {

  private String csrfToken;

  @Pattern(message = "\"name\" has errors.", regexp = "^[a-zA-Z]{6,}$")
  private String name;

  @Pattern(message = "\"email\" has errors.",
      regexp = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+"
          + "(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+"
          + "(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$")
  private String email;

  public String getCsrfToken() {
    return csrfToken;
  }

  public void setCsrfToken(String csrfToken) {
    this.csrfToken = csrfToken;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
