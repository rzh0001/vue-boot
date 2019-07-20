package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.Member;
import org.jeecg.modules.pay.mapper.MemberMapper;
import org.jeecg.modules.pay.service.IMemberService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2019-07-20
 * @Version: V1.0
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

}
