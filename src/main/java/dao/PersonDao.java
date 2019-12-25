package dao;

import entity.Person;
import framework.annotation.Autowired;
import framework.annotation.Component;
import framework.pool.PoolManageImpl;

@Component
public class PersonDao {
    @Autowired
    private PoolManageImpl pool;

    /**
     * 保存
     *
     * @param person
     * @return
     */
    public int save(Person person) {
        return pool.save(person);
    }

    /**
     * 查询
     *
     * @param id
     * @return
     */
    public Person findById(int id) {
        return pool.get(id, Person.class);
    }
}
