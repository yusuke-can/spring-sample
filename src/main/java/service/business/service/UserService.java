package service.business.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import service.business.model.Member;
import service.dao.spring.UserDao;


//本当はインターフェースからimplemetsすべきですが、ここでは簡略のためそのままクラスを作っています。
@Service
public class UserService {
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public Member getUser(int id){
        return this.userDao.getUser(id);
    }

    public void updateUser(Member user){
        this.userDao.updateUser(user);
    }
}
