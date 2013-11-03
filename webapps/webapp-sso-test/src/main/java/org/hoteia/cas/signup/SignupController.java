package org.hoteia.cas.signup;

import org.hoteia.cas.account.*;
import org.hoteia.cas.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class SignupController {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "signup")
	public SignupForm signup() {
		return new SignupForm();
	}
	
	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return null;
		}
		
		Account account = accountRepository.save(signupForm.createAccount());
		userService.signin(account);

        MessageHelper.addSuccessAttribute(ra, "Congratulations! You have successfully signed up.");
		
		return "redirect:/";
	}
}
