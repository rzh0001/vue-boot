package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.vo.SysUserPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<SysUser> {
	void cleanGoogle(String username);

	void updateUserGoogleKey(String userName, String googleKey);

	public SysUser getUserByName(String username);

	public SysUser getUserById(String id);

	/**
	 * 添加用户和用户角色关系
	 *
	 * @param user
	 * @param roles
	 */
	public void addUserWithRole(SysUser user, String roles);


	/**
	 * 修改用户和用户角色关系
	 *
	 * @param user
	 * @param roles
	 */
	public void editUserWithRole(SysUser user, String roles);

	/**
	 * 获取用户的授权角色
	 *
	 * @param username
	 * @return
	 */
	public List<String> getRole(String username);

	/**
	 * 查询用户信息包括 部门信息
	 *
	 * @param username
	 * @return
	 */
	public SysUserCacheInfo getCacheUser(String username);

	/**
	 * 根据部门Id查询
	 *
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username);

	/**
	 * 根据角色Id查询
	 *
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username);

	/**
	 * 通过用户名获取用户角色集合
	 *
	 * @param username 用户名
	 * @return 角色集合
	 */
	Set<String> getUserRolesSet(String username);

	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	Set<String> getUserPermissionsSet(String username);

	/**
	 * 根据用户名设置部门ID
	 *
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(String username, String orgCode);

	/**
	 * 根据手机号获取用户名和密码
	 */
	public SysUser getUserByPhone(String phone);


	/**
	 * 根据邮箱获取用户
	 */
	public SysUser getUserByEmail(String email);


	/**
	 * 添加用户和用户部门关系
	 *
	 * @param user
	 * @param selectedParts
	 */
	void addUserWithDepart(SysUser user, String selectedParts);

	/**
	 * 编辑用户和用户部门关系
	 *
	 * @param user
	 * @param departs
	 */
	void editUserWithDepart(SysUser user, String departs);

	/**
	 * 校验用户是否有效
	 *
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(SysUser sysUser);

	public void addPayMember(SysUser user, String id);

	/**
	 * 通过代理获取代理下面的所有商户
	 *
	 * @param agentName
	 * @return
	 */
	List<SysUser> getUserByAgent(String agentName);

	List<SysUser> getUserAndReferByAgent(String agentName);

	List<String> getUserByRefer(String refer);

	/**
	 * 翻页查询
	 *
	 * @param page 翻页对象
	 * @param map
	 */
	IPage<SysUserPage> pageUserWithPaymentInfo(Page page, Wrapper wrapper);

	/**
	 * 手工调账
	 *
	 * @param username
	 * @param adjustAmount
	 * @return
	 */
	boolean adjustAmount(String username, BigDecimal adjustAmount, String remark);
}
