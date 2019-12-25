package controller;

import entity.Person;
import framework.annotation.Autowired;
import framework.annotation.Component;
import framework.annotation.Path;
import service.PersonServiceImpl;

@Path("/person")
@Component
public class PersonController {
    @Autowired
    private PersonServiceImpl personService;

    @Path("/add")
    public String add(String name,Integer age){
        Person person=new Person("ceshi",11);
        Integer id = personService.save(person);
        return id.toString();
    }
}
