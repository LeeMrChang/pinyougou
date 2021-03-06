package com.pinyougou.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.pinyougou.user.service.UserService;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {


    private TbUserMapper userMapper;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
                //criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                criteria.andLike("password", "%" + user.getPassword() + "%");
                //criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                criteria.andLike("phone", "%" + user.getPhone() + "%");
                //criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                criteria.andLike("email", "%" + user.getEmail() + "%");
                //criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if (StringUtils.isNotBlank(user.getSourceType())) {
                criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(user.getNickName())) {
                criteria.andLike("nickName", "%" + user.getNickName() + "%");
                //criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if (StringUtils.isNotBlank(user.getName())) {
                criteria.andLike("name", "%" + user.getName() + "%");
                //criteria.andNameLike("%"+user.getName()+"%");
            }
            if (StringUtils.isNotBlank(user.getStatus())) {
                criteria.andLike("status", "%" + user.getStatus() + "%");
                //criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(user.getHeadPic())) {
                criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
                //criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if (StringUtils.isNotBlank(user.getQq())) {
                criteria.andLike("qq", "%" + user.getQq() + "%");
                //criteria.andQqLike("%"+user.getQq()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsMobileCheck())) {
                criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
                //criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsEmailCheck())) {
                criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
                //criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getSex())) {
                criteria.andLike("sex", "%" + user.getSex() + "%");
                //criteria.andSexLike("%"+user.getSex()+"%");
            }

        }

        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;

    //注入mq  生产者对象
    @Autowired
    private DefaultMQProducer producer;

    @Value("${sign_name}")
    private String sign_name;

    @Value("${template_code}")
    private String template_code;

    /**
     * 1.根据手机号码，生产验证码
     * 2.将验证码 存储到redis中
     * 3.将验证码 和手机码、签名、模板的内容 作为消息体 发送消息到mq
     * 3.1 集成生成mq，加入依赖
     * 3.2 配置spring的配置文件（配置生产的对象）
     *
     * @param phone
     */
    @Override
    public void createSmsCode(String phone) {

        try {
            //1.生成6位数的随机验证码
            String code = (long) ((Math.random() * 9 + 1) * 100000) + "";

            //2.将验证码存储到redis中
            redisTemplate.boundValueOps("Register_" + phone).set(code);

            //设置验证码在redis的生命周期,24L代表存活1小时
            redisTemplate.boundValueOps("Register_"+phone).expire(24L, TimeUnit.HOURS);

            //3.打印测试  验证码的生成
            System.out.println(redisTemplate.boundValueOps("Register_" + phone).get());

            //4.使用mq生成者对象发送消息
            //消息体body中有很多内容(消息本身、手机号、模板、验证码)，需要封装，使用map对象接收
            Map<String, String> messageInfo = new HashMap<>();
            //封装消息体
            messageInfo.put("mobile", phone);  //封装电话
            messageInfo.put("sign_name", sign_name);  //封装签名
            messageInfo.put("template_code", template_code); //封装模板CODE
            messageInfo.put("param", "{\"code\":\"" + code + "\"}");  //设置json字符串的参数

            //将封装好的消息体内容转成json字符创的格式
            String jsonStr = JSON.toJSONString(messageInfo);
/*c+f11  c+num*/
            Message message = new Message(
                    "SMS_TOPIC",  //消息主题
                    "SEND_MESSAGE_TAG",  //消息标签 tag
                    "createSmsCode",  //消息的keys 唯一标识
                    jsonStr.getBytes());  //消息体 body

            //发送消息的send
            producer.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 比对验证验证码的方法
     *
     * @param phone
     * @param smsCode
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {

        //如果手机号码与验证码有一个为空，则数据错误，返回false
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsCode)){
                return false;
        }
        //往下，两个都不为空
        //获取redis中的验证码
        String code = (String) redisTemplate.boundValueOps("Register_" + phone).get();

        //与页面输入的验证码进行比对
        if(!code.equals(smsCode)){//如果不一致
           return false;
        }
        //做判断对错的时候，一般先取反，先判断为false的情况
        return true;
    }


}
