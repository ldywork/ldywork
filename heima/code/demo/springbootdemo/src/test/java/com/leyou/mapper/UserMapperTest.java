package com.leyou.mapper;


import com.leyou.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
public class UserMapperTest {
//    @Deployment
//    public static JavaArchive createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class)
//                .addClass(UserMapper.class)
//                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//    }
    @Autowired
    private UserMapper userMapper;
    @Test
    public void getMessage(){
        List<User> users = userMapper.selectAll();
        System.out.println(users);
    }

}
