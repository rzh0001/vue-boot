<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="12">
            <a-form-item label="账号">
              <a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>

          <!--          <a-col :md="6" :sm="8">-->
          <!--            <a-form-item label="性别">-->
          <!--              <a-select v-model="queryParam.sex" placeholder="请选择性别查询">-->
          <!--                <a-select-option value="">请选择性别查询</a-select-option>-->
          <!--                <a-select-option value="1">男性</a-select-option>-->
          <!--                <a-select-option value="2">女性</a-select-option>-->
          <!--              </a-select>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->


          <template v-if="toggleSearchStatus">
            <!--            <a-col :md="6" :sm="8">-->
            <!--              <a-form-item label="邮箱">-->
            <!--                <a-input placeholder="请输入邮箱查询" v-model="queryParam.email"></a-input>-->
            <!--              </a-form-item>-->
            <!--            </a-col>-->

            <a-col :md="6" :sm="8">
              <a-form-item label="手机号码">
                <a-input placeholder="请输入手机号码查询" v-model="queryParam.phone"></a-input>
              </a-form-item>
            </a-col>

            <a-col :md="6" :sm="8">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="请选择用户状态查询">
                  <a-select-option value="">请选择用户状态</a-select-option>
                  <a-select-option value="1">正常</a-select-option>
                  <a-select-option value="2">解冻</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <a-button @click="handleAdd" v-has="'user:add'" type="primary" icon="plus">添加用户</a-button>
      <a-button @click="handleAddAgent" v-has="'user:addAgent'" type="primary" icon="plus">添加代理</a-button>
      <a-button @click="handleAddSalesman" v-has="'user:addSalesman'" type="primary" icon="plus">添加介绍人</a-button>
      <a-button @click="handleAddMember" v-has="'user:addMember'" type="primary" icon="plus">添加商户</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"-->
<!--                @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay" @click="handleMenuClick">
          <a-menu-item key="1">
            <a-icon type="delete" @click="batchDel"/>
            删除
          </a-menu-item>
          <a-menu-item key="2">
            <a-icon type="lock" @click="batchFrozen('2')"/>
            冻结
          </a-menu-item>
          <a-menu-item key="3">
            <a-icon type="unlock" @click="batchFrozen('1')"/>
            解冻
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
<!--      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">-->
<!--        <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;<a style="font-weight: 600">{{-->
<!--        selectedRowKeys.length }}</a>项&nbsp;&nbsp;-->
<!--        <a style="margin-left: 24px" @click="onClearSelected">清空</a>-->
<!--      </div>-->

      <a-table
        ref="table"
        bordered
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">


        <span slot="action" slot-scope="text, record">

<!--          <a @click="handleDetail(record)">详情</a>-->
          <!--          <a-divider type="vertical"/>-->
          <!--          <a @click="handleEdit(record)">编辑</a>-->
          <!--          <a-divider type="vertical"/>-->

          <a-dropdown>
<!--            <a class="ant-dropdown-link">-->
            <!--              更多 <a-icon type="down"/>-->
            <!--            </a>-->
            <a-button>
             帐号管理 <!-- <a-icon type="down"/>-->
            </a-button>
            <a-menu slot="overlay">

              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleEdit(record)">编辑</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;" @click="handleChangePassword(record.username)">密码</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定重置谷歌密钥吗?" @confirm="() => cleanGoogle(record.username)">
                  <a>重置谷歌密钥</a>
                </a-popconfirm>
              </a-menu-item>
              <!--<a-menu-item>-->
                <!--<a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
                  <!--<a>删除</a>-->
                <!--</a-popconfirm>-->
              <!--</a-menu-item>-->

              <a-menu-item v-if="record.status==1">
                <a-popconfirm title="确定冻结吗?" @confirm="() => handleFrozen(record.id,2)">
                  <a>冻结</a>
                </a-popconfirm>
              </a-menu-item>

              <a-menu-item v-if="record.status==2">
                <a-popconfirm title="确定解冻吗?" @confirm="() => handleFrozen(record.id,1)">
                  <a>解冻</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
          <a-dropdown>
            <a-button>
              通道配置
            </a-button>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="channelDetail(record)">已配置通道</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="addChannel(record)">添加通道</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
         <a-dropdown>
            <a-button >
              关联子账号
            </a-button>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="activeBusiness(record)">激活子账号</a>
              </a-menu-item>
               <a-menu-item>
                <a @click="rechargeAmount(record)">子账号充值</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
            <a-dropdown>
            <a-button>
              费率设置
            </a-button>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="rateDeatil(record)">已设置费率</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="addRate(record)">添加费率</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->

    <user-modal ref="modalForm" @ok="modalFormOk"></user-modal>

    <user-agent-modal ref="agentModalForm" @ok="modalFormOk"></user-agent-modal>
    <user-salesman-modal ref="salesmanModalForm" @ok="modalFormOk"></user-salesman-modal>
    <user-member-modal ref="memberModalForm" @ok="modalFormOk"></user-member-modal>

    <password-modal ref="passwordmodal" @ok="passwordModalOk"></password-modal>

    <sys-user-agent-modal ref="sysUserAgentModal"></sys-user-agent-modal>
    <user-channel-modal ref="userChannelModal"></user-channel-modal>
    <user-business-modal ref="userBusinessModal"></user-business-modal>
    <user-rate-modal ref="userRateModal"></user-rate-modal>
    <active-business-modal ref="activeBusinessModal"></active-business-modal>
  </a-card>
</template>

<script>
  import UserModal from './modules/UserModal'
  import UserAgentModal from './modules/UserAgentModal'
  import UserSalesmanModal from './modules/UserSalesmanModal'
  import UserMemberModal from './modules/UserMemberModal'
  import PasswordModal from './modules/PasswordModal'
  import { putAction } from '@/api/manage'
  import { frozenBatch } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import SysUserAgentModal from './modules/SysUserAgentModal'
  import UserChannelModal from './modules/UserChannelModal'
  import UserBusinessModal from './modules/UserBusinessModal'
  import UserRateModal from './modules/UserRateModal'
  import ActiveBusinessModal from './modules/ActiveBusinessModal'
  import {getAction,httpAction} from '@/api/manage'

  export default {
    name: 'UserList',
    mixins: [JeecgListMixin],
    components: {
      SysUserAgentModal,
      UserModal,
      UserAgentModal,
      UserSalesmanModal,
      UserMemberModal,
      PasswordModal,
      UserChannelModal,
      UserBusinessModal,
      UserRateModal,
      ActiveBusinessModal
    },
    data() {
      return {
        description: '这是用户管理页面',
        queryParam: {},
        isAgent:true,
        columns: [
          {
            title: '用户账号',
            align: 'center',
            dataIndex: 'username',
            width: 80
          },
          {
            title: '真实姓名',
            align: 'center',
            width: 100,
            dataIndex: 'realname'
          },
          {
            title: '会员类型',
            align: 'center',
            width: 80,
            dataIndex: 'memberType',
            key: 'memberType',
            customRender: function(text) {
              if (text == 1) {

                return '代理'
              } else if (text == 2) {
                return '介绍人'
              } else if (text == 3) {
                return '商户'
              } else {
                return text
              }
            }
          },

          {
            title: '上级代理',
            align: 'center',
            width: 80,
            dataIndex: 'agentRealname',
            sorter: true
          },
          {
            title: '介绍人',
            align: 'center',
            width: 80,
            dataIndex: 'salesmanRealname'
          },
          {
            title: '可提现金额',
            align: 'center',
            width: 80,
            dataIndex: 'amount'
          },
          {
            title: '状态',
            align: 'center',
            width: 60,
            dataIndex: 'status_dictText'
          },
          {
            title: '操作',
            // fixed: 'right',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            align: 'center',
            width: 300
          }

        ],
        url: {
          imgerver: window._CONFIG['domianURL'] + '/sys/common/view',
          syncUser: '/process/extActProcess/doSyncUser',
          list: '/sys/user/list',
          delete: '/sys/user/delete',
          deleteBatch: '/sys/user/deleteBatch',
          exportXlsUrl: '/sys/user/exportXls',
          importExcelUrl: 'sys/user/importExcel',
          cleanGoogle:'/sys/user/cleanGoogle'
        }
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      rateDeatil:function(record){
        this.$refs.userRateModal.title='已添加费率详情';
        this.$refs.userRateModal.detail(record);
      },
      addRate: function(record){
        this.$refs.userRateModal.title='添加费率';
        this.$refs.userRateModal.addRate(record);
      },
      businessDeatil:function(record){
        if(record.memberType != "1"){
          alert("会员类型不是代理，无挂马信息");
          return;
        }
        this.$refs.userBusinessModal.title='已添加挂马详情';
        this.$refs.userBusinessModal.detail(record);
      },
      addBusiness:function(record){
        if(record.memberType != "1"){
          alert("无操作权限");
          return;
        }
        this.$refs.userBusinessModal.title='添加挂马信息';
        this.$refs.userBusinessModal.addUserBusiness(record);
      },
      channelDetail: function(record){
        this.$refs.userChannelModal.detail(record);
      },
      addChannel: function(record){
        this.$refs.userChannelModal.addChannel(record);
      },
      activeBusiness: function(record){
        if(record.memberType != "1"){
          alert("无操作权限");
          return;
        }
        this.$refs.activeBusinessModal.activeBusiness(record);
      },
      rechargeAmount: function(record){
        if(record.memberType != "1"){
          alert("无操作权限");
          return;
        }
        this.$refs.activeBusinessModal.rechargeAmount(record);
      },
      getAvatarView: function(avatar) {
        return this.url.imgerver + '/' + avatar
      },

      batchFrozen: function(status) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！')
          return false
        } else {
          let ids = ''
          let that = this
          that.selectedRowKeys.forEach(function(val) {
            ids += val + ','
          })
          that.$confirm({
            title: '确认操作',
            content: '是否' + (status == 1 ? '解冻' : '冻结') + '选中账号?',
            onOk: function() {
              frozenBatch({ ids: ids, status: status }).then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.loadData()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.message)
                }
              })
            }
          })
        }
      },
      handleAddAgent: function() {
        this.$refs.agentModalForm.add()
        this.$refs.agentModalForm.title = '新增代理'
        this.$refs.agentModalForm.disableSubmit = false
      },
      handleAddSalesman: function() {
        this.$refs.salesmanModalForm.add()
        this.$refs.salesmanModalForm.title = '新增介绍人'
        this.$refs.salesmanModalForm.disableSubmit = false
      },
      handleAddMember: function() {
        this.$refs.memberModalForm.add()
        this.$refs.memberModalForm.title = '新增商户'
        this.$refs.memberModalForm.disableSubmit = false
      },
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel()
        } else if (e.key == 2) {
          this.batchFrozen(2)
        } else if (e.key == 3) {
          this.batchFrozen(1)
        }
      },
      handleFrozen: function(id, status) {
        let that = this
        frozenBatch({ ids: id, status: status }).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.loadData()
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      cleanGoogle:function(name){
        var params = {username:name};//查询条件
        getAction(this.url.cleanGoogle,params).then((res)=>{
          if(res.success){
            alert(res.result)
        }else{
        }
      })
      },
      handleChangePassword(username) {
        this.$refs.passwordmodal.show(username)
      },
      handleAgentSettings(username) {
        this.$refs.sysUserAgentModal.agentSettings(username)
        this.$refs.sysUserAgentModal.title = '用户代理人设置'
      },
      passwordModalOk() {
        //TODO 密码修改完成 不需要刷新页面，可以把datasource中的数据更新一下
      }
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>