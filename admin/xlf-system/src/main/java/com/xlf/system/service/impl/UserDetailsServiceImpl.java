package com.xlf.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.system.domain.UserDetails;
import com.xlf.system.service.UserDetailsService;
import com.xlf.system.mapper.UserDetailsMapper;
import org.springframework.stereotype.Service;

/**
* @author 小新
* @description 针对表【user_details】的数据库操作Service实现
* @createDate 2023-03-15 15:04:16
*/
@Service(value = "userDetailsServiceImpl2")
public class UserDetailsServiceImpl extends ServiceImpl<UserDetailsMapper, UserDetails>
    implements UserDetailsService{

}




