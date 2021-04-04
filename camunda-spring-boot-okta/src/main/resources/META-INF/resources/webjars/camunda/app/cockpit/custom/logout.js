const doFetch = window.fetch;

window.fetch = function() {
	// Intercept calling logout
    if (arguments[0] === '/camunda/api/admin/auth/user/default/logout') {    
      	var logoutForm = document.createElement("form");
      	logoutForm.setAttribute("method", "POST");
      	logoutForm.setAttribute("action", "/logout");
      	document.getElementsByTagName("body")[0].appendChild(logoutForm);
     	logoutForm.submit();
    } else {
      return doFetch.apply(this, arguments)
    }
}