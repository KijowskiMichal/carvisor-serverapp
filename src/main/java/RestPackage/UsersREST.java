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

    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return userService.list(request,page,pageSize,regex);
    }

    @RequestMapping(value = "/listUserNames/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listUserNames(HttpServletRequest request, @PathVariable("regex") String regex) {
        return userService.listUserNames(request,regex);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity changePassword(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return userService.changePassword(request, httpEntity);
    }

    @RequestMapping(value = "/changeNick", method = RequestMethod.POST)
    public ResponseEntity changeNick(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return userService.changeNick(request, httpEntity);
    }

    @RequestMapping(value = "/changeUserData/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeUserData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return userService.changeUserData(request,httpEntity,userID);
    }

    @RequestMapping(value = "/changeUserImage/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeUserImage(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return userService.changeUserImage(request,httpEntity,userID);
    }

    @RequestMapping(value = "/getUserData/{id}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity getUserData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return userService.getUserData(request, httpEntity, userID);
    }

    @RequestMapping(value = "/addUser", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity addUser(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return userService.addUser(request, httpEntity);
    }
}
