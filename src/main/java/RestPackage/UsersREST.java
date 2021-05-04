package RestPackage;

import Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * REST controller responsible for user management.
 */
@RestController
@RequestMapping("/users")
public class UsersREST {
    private final UserService userService;

    @Autowired
    public UsersREST(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param page     Page of users list. Parameter associated with pageSize.
     * @param pageSize Number of record we want to get
     * @param regex    Part of name or surname we want to display
     * @return Returns the contents of the page that contains a list of users in the JSON format.
     *
     * WebMethod which returns a list of users.
     */
    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return userService.list(request,page,pageSize,regex);
    }

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return HttpStatus.UNAUTHORIZED if session not found, HttpStatus.OK if all is ok, BAD_REQUEST if json haven't required data or password don't match
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity changePassword(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return userService.changePassword(request, httpEntity);
    }
}
