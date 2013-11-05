package org.hoteia.cas.signup;

import javax.validation.Valid;

import org.hoteia.cas.account.AccountRepository;
import org.hoteia.cas.account.UserService;
import org.hoteia.cas.web.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupController {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserService userService;

	@RequestMapping("signup")
	public SignupForm signup() {
		return new SignupForm();
	}
	
	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return null;
		}
		
		userService.saveUser(signupForm.createAccount());
        MessageHelper.addSuccessAttribute(ra, "Congratulations! You have successfully signed up.");
		
		return "redirect:/";
	}
}
