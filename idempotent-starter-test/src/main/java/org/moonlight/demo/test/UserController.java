package org.moonlight.demo.test;

import org.moonlight.idempotent.annotation.Idempotent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Moonlight
 */
@RestController
public class UserController {

    @Idempotent
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody User user) {
        System.out.println("save user " + user);
        Map<String, Object> res = new HashMap<>();
        res.put("msg", "save user success");
        res.put("info", user);
        return res;
    }

}
