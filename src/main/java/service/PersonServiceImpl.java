package service;

import dao.PersonDao;
import entity.Person;
import framework.annotation.Autowired;
import framework.annotation.Component;

@Component
public class PersonServiceImpl {
    @Autowired
    private PersonDao personDao;

    /**
     * 保存
     *
     * @param person
     * @return
     */
    public Integer save(Person person) {
        return personDao.save(person);
    }

    /**
     * 查询
     *
     * @param id
     * @return
     */
    public Person get(Integer id) {
        return personDao.findById(id);
    }
}
