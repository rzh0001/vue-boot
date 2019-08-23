package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.service.ISysRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.SysUserPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
	
	@Autowired
	private SysUserMapper userMapper;
	@Autowired
	private SysPermissionMapper sysPermissionMapper;
	@Autowired
	private ISysRoleService sysRoleService;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private SysUserDepartMapper sysUserDepartMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private SysDepartMapper sysDepartMapper;
	@Autowired
	private IUserAmountEntityService userAmountService;
	
	@Override
	public SysUser getUserByName(String username) {
		return userMapper.getUserByName(username);
	}
	
	
	@Override
	@Transactional
	public void addUserWithRole(SysUser user, String roles) {
		this.save(user);
		if(oConvertUtils.isNotEmpty(roles)) {
			String[] arr = roles.split(",");
			for (String roleId : arr) {
				SysUserRole userRole = new SysUserRole(user.getId(), roleId);
				sysUserRoleMapper.insert(userRole);
			}
		}
	}
	
	@Override
	@CacheEvict(value= CacheConstant.LOGIN_USER_RULES_CACHE, allEntries=true)
	@Transactional
	public void editUserWithRole(SysUser user, String roles) {
		this.updateById(user);
		//先删后加
		sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getId()));
		if(oConvertUtils.isNotEmpty(roles)) {
			String[] arr = roles.split(",");
			for (String roleId : arr) {
				SysUserRole userRole = new SysUserRole(user.getId(), roleId);
				sysUserRoleMapper.insert(userRole);
			}
		}
	}
	
	
	@Override
	public List<String> getRole(String username) {
		return sysUserRoleMapper.getRoleByUserName(username);
	}
	
	/**
	 * 通过用户名获取用户角色集合
	 * @param username 用户名
	 * @return 角色集合
	 */
	@Override
	@Cacheable(value = CacheConstant.LOGIN_USER_RULES_CACHE,key = "'Roles_'+#username")
	public Set<String> getUserRolesSet(String username) {
		// 查询用户拥有的角色集合
		List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
		log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
		return new HashSet<>(roles);
	}
	
	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	@Override
	@Cacheable(value = CacheConstant.LOGIN_USER_RULES_CACHE,key = "'Permissions_'+#username")
	public Set<String> getUserPermissionsSet(String username) {
		Set<String> permissionSet = new HashSet<>();
		List<SysPermission> permissionList = sysPermissionMapper.queryByUser(username);
		for (SysPermission po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
			if (oConvertUtils.isNotEmpty(po.getPerms())) {
				permissionSet.add(po.getPerms());
			}
		}
		log.info("-------通过数据库读取用户拥有的权限Perms------username： "+ username+",Perms size: "+ (permissionSet==null?0:permissionSet.size()) );
		return permissionSet;
	}
	
	@Override
	public SysUserCacheInfo getCacheUser(String username) {
		SysUserCacheInfo info = new SysUserCacheInfo();
		info.setOneDepart(true);
//		SysUser user = userMapper.getUserByName(username);
//		info.setSysUserCode(user.getUsername());
//		info.setSysUserName(user.getRealname());
		
		
		LoginUser user = sysBaseAPI.getUserByName(username);
		if(user!=null) {
			info.setSysUserCode(user.getUsername());
			info.setSysUserName(user.getRealname());
			info.setSysOrgCode(user.getOrgCode());
		}
		
		//多部门支持in查询
		List<SysDepart> list = sysDepartMapper.queryUserDeparts(user.getId());
		List<String> sysMultiOrgCode = new ArrayList<String>();
		if(list==null || list.size()==0) {
			//当前用户无部门
			//sysMultiOrgCode.add("0");
		}else if(list.size()==1) {
			sysMultiOrgCode.add(list.get(0).getOrgCode());
		}else {
			info.setOneDepart(false);
			for (SysDepart dpt : list) {
				sysMultiOrgCode.add(dpt.getOrgCode());
			}
		}
		info.setSysMultiOrgCode(sysMultiOrgCode);
		
		return info;
	}
	
	// 根据部门Id查询
	@Override
	public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username) {
		return userMapper.getUserByDepId(page, departId,username);
	}
	
	
	// 根据角色Id查询
	@Override
	public IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username) {
		return userMapper.getUserByRoleId(page,roleId,username);
	}
	
	
	@Override
	public void updateUserDepart(String username, String orgCode) {
		baseMapper.updateUserDepart(username, orgCode);
	}
	
	
	@Override
	public SysUser getUserByPhone(String phone) {
		return userMapper.getUserByPhone(phone);
	}
	
	
	@Override
	public SysUser getUserByEmail(String email) {
		return userMapper.getUserByEmail(email);
	}
	
	@Override
	@Transactional
	public void addUserWithDepart(SysUser user, String selectedParts) {
//		this.save(user);  //保存角色的时候已经添加过一次了
		if(oConvertUtils.isNotEmpty(selectedParts)) {
			String[] arr = selectedParts.split(",");
			for (String deaprtId : arr) {
				SysUserDepart userDeaprt = new SysUserDepart(user.getId(), deaprtId);
				sysUserDepartMapper.insert(userDeaprt);
			}
		}
	}
	
	
	@Override
	@Transactional
	@CacheEvict(value="loginUser_cacheRules", allEntries=true)
	public void editUserWithDepart(SysUser user, String departs) {
		this.updateById(user);  //更新角色的时候已经更新了一次了，可以再跟新一次
		//先删后加
		sysUserDepartMapper.delete(new QueryWrapper<SysUserDepart>().lambda().eq(SysUserDepart::getUserId, user.getId()));
		if(oConvertUtils.isNotEmpty(departs)) {
			String[] arr = departs.split(",");
			for (String departId : arr) {
				SysUserDepart userDepart = new SysUserDepart(user.getId(), departId);
				sysUserDepartMapper.insert(userDepart);
			}
		}
	}
	
	
	/**
	 * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	@Override
	public Result<?> checkUserIsEffective(SysUser sysUser) {
		Result<?> result = new Result<Object>();
		//情况1：根据用户信息查询，该用户不存在
		if (sysUser == null) {
			result.error500("该用户不存在，请注册");
			sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
			return result;
		}
		//情况2：根据用户信息查询，该用户已注销
		if (CommonConstant.DELETED_FLAG.toString().equals(sysUser.getDelFlag())) {
			sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已注销");
			return result;
		}
		//情况3：根据用户信息查询，该用户已冻结
		if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
			sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已冻结");
			return result;
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString().replaceAll("-","").substring(0,16));
	}
	
	@Override
	public void addPayMember(SysUser user, String memberType) {
		user.setCreateTime(new Date());
		user.setSalt(oConvertUtils.randomGen(8));
		String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), user.getSalt());
		user.setPassword(passwordEncode);
		user.setStatus(CommonConstant.USER_UNFREEZE);
		user.setDelFlag(CommonConstant.NOT_DELETE_FLAG);
		user.setMemberType(memberType);
		
		LoginUser optUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String roleCode = "";
		switch (memberType) {
			case PayConstant.MEMBER_TYPE_AGENT:
				roleCode = PayConstant.ROLE_CODE_AGENT;
				user.setApiKey(UUID.randomUUID().toString().replaceAll("-","").substring(0,16));
				break;
			case PayConstant.MEMBER_TYPE_SALESMAN:
				roleCode = PayConstant.ROLE_CODE_SALESMAN;
				user.setAgentId(optUser.getId());
				user.setAgentUsername(optUser.getUsername());
				user.setAgentRealname(optUser.getRealname());
				break;
			case PayConstant.MEMBER_TYPE_MEMBER:
				roleCode = PayConstant.ROLE_CODE_MEMBER;
				user.setApiKey(UUID.randomUUID().toString().replaceAll("-","").substring(0,16));
				user.setAgentId(optUser.getId());
				user.setAgentUsername(optUser.getUsername());
				user.setAgentRealname(optUser.getRealname());
				if (StringUtils.isNotBlank(user.getSalesmanId())) {
					SysUser salesman = getById(user.getSalesmanId());
					user.setSalesmanUsername(salesman.getUsername());
					user.setSalesmanRealname(salesman.getRealname());
				}
				break;
			default:
		}
		
		SysRole role = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
		Optional<SysRole> opt = Optional.ofNullable(role);
		if (!opt.isPresent()) {
//			result.fail("支付平台角色配置出错！");
		}
		this.addUserWithRole(user, opt.get().getId());
		
		//生成余额表
		userAmountService.initialUserAmount(this.getUserByName(user.getUsername()));
	}
	
	@Override
	public List<SysUser> getUserByAgent(String agentName) {
		return userMapper.getUserByAgent(agentName);
	}
	
	@Override
	public List<SysUser> getUserAndReferByAgent(String agentName) {
		return userMapper.getUserAndReferByAgent(agentName);
	}
	
	@Override
	public List<String> getUserByRefer(String refer) {
		return userMapper.getUserByRefer(refer);
	}
	
	@Override
	public IPage<SysUserPage> pageUserWithPaymentInfo(Page page, SysUser user) {
		return userMapper.listUserWithPaymentInfo(page, user);
	}

//    @Override
//	public IPage<List<Map<String, Object>>> pageUserWithPaymentInfo(Page page, Wrapper<SysUser> queryWrapper) {
//		return userMapper.listUserWithPaymentInfo(page, queryWrapper);
//}
	
}
