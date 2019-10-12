package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.vo.SysUserPage;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Update("update sys_user set google_secret_key= null where  username=#{username}")
    void cleanGoogle(@Param("username")String username);
    @Update("update sys_user set google_secret_key=#{googleKey} where username=#{userName}")
    void updateUserGoogleKey(@Param("userName")String userName, @Param("googleKey")String googleKey);
    /**
     * 通过用户账号查询用户信息
     *
     * @param username
     * @return
     */
    public SysUser getUserByName(@Param("username") String username);

    @Select("select * from sys_user where id = #{id}")
    public SysUser  getUserById(@Param("id")String id);
    /**
     * @param page
     * @param map
     * @return
     */
    IPage<SysUserPage> listUserWithPaymentInfo(Page page, @Param("map") Map<String, Object> map);
    
    /**
     * 根据部门Id查询用户信息
     *
     * @param page
     * @param departId
     * @return
     */
    IPage<SysUser> getUserByDepId(Page page, @Param("departId") String departId, @Param("username") String username);

    /**
     * 根据角色Id查询用户信息
     *
     * @param page
     * @param
     * @return
     */
    IPage<SysUser> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);

    /**
     * 根据用户名设置部门ID
     *
     * @param username
     * @param departId
     */
    void updateUserDepart(@Param("username") String username, @Param("orgCode") String orgCode);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone
     * @return
     */
    public SysUser getUserByPhone(@Param("phone") String phone);


    /**
     * 根据邮箱查询用户信息
     *
     * @param email
     * @return
     */
    public SysUser getUserByEmail(@Param("email") String email);

    /**
     * 通过代理获取代理下面的所有商户
     *
     * @param agentName
     * @return
     */
    List<SysUser> getUserByAgent(@Param("agentName") String agentName);

    List<SysUser> getUserAndReferByAgent(@Param("agentName") String agentName);

    List<String> getUserByRefer(@Param("refer") String refer);
}
