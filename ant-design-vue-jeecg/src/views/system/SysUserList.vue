<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="登录账号">
              <a-input placeholder="请输入登录账号" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="真实姓名">
              <a-input placeholder="请输入真实姓名" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="密码">
                <a-input placeholder="请输入密码" v-model="queryParam.password"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="md5密码盐">
                <a-input placeholder="请输入md5密码盐" v-model="queryParam.salt"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="头像">
                <a-input placeholder="请输入头像" v-model="queryParam.avatar"></a-input>
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
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('用户表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"
                @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
        selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <sysUser-modal ref="modalForm" @ok="modalFormOk"></sysUser-modal>
  </a-card>
</template>

<script>
  import SysUserModal from './modules/SysUserModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'SysUserList',
    mixins: [JeecgListMixin],
    components: {
      SysUserModal
    },
    data() {
      return {
        description: '用户表管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '登录账号',
            align: 'center',
            dataIndex: 'username'
          },
          {
            title: '真实姓名',
            align: 'center',
            dataIndex: 'realname'
          },
          // {
          //   title: '密码',
          //   align: 'center',
          //   dataIndex: 'password'
          // },
          // {
          //   title: 'md5密码盐',
          //   align: 'center',
          //   dataIndex: 'salt'
          // },
          // {
          //   title: '头像',
          //   align: 'center',
          //   dataIndex: 'avatar'
          // },
          // {
          //   title: '生日',
          //   align: 'center',
          //   dataIndex: 'birthday'
          // },
          // {
          //   title: '性别（1：男 2：女）',
          //   align: 'center',
          //   dataIndex: 'sex'
          // },
          {
            title: '电子邮件',
            align: 'center',
            dataIndex: 'email'
          },
          {
            title: '电话',
            align: 'center',
            dataIndex: 'phone'
          },
          {
            title: '部门code',
            align: 'center',
            dataIndex: 'orgCode'
          },
          {
            title: '状态(1：正常  2：冻结 ）',
            align: 'center',
            dataIndex: 'status'
          },
          {
            title: '删除状态（0，正常，1已删除）',
            align: 'center',
            dataIndex: 'delFlag'
          },
          {
            title: '同步工作流引擎1同步0不同步',
            align: 'center',
            dataIndex: 'activitiSync'
          },
          {
            title: 'userId',
            align: 'center',
            dataIndex: 'userId'
          },
          {
            title: '会员类型(1：代理  2：介绍人 3：商户）',
            align: 'center',
            dataIndex: 'memberType'
          },
          {
            title: '单笔金额上限',
            align: 'center',
            dataIndex: 'upperLimit'
          },
          {
            title: '单笔金额下限',
            align: 'center',
            dataIndex: 'lowerLimit'
          },
          {
            title: '代理ID',
            align: 'center',
            dataIndex: 'agentId'
          },
          {
            title: '代理帐号',
            align: 'center',
            dataIndex: 'agentUsername'
          },
          {
            title: '代理姓名',
            align: 'center',
            dataIndex: 'agentRealname'
          },
          {
            title: '介绍人ID',
            align: 'center',
            dataIndex: 'salesmanId'
          },
          {
            title: '介绍人帐号',
            align: 'center',
            dataIndex: 'salesmanUsername'
          },
          {
            title: '介绍人姓名',
            align: 'center',
            dataIndex: 'salesmanRealname'
          },
          // {
          //   title: '谷歌密钥',
          //   align: 'center',
          //   dataIndex: 'googleSecretKey'
          // },
          // {
          //   title: '支付密码',
          //   align: 'center',
          //   dataIndex: 'paymentPassword'
          // },
          {
            title: 'IP白名单开关',
            align: 'center',
            dataIndex: 'ipSwitch'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: '/system/sysUser/list',
          delete: '/system/sysUser/delete',
          deleteBatch: '/system/sysUser/deleteBatch',
          exportXlsUrl: 'system/sysUser/exportXls',
          importExcelUrl: 'system/sysUser/importExcel'
        }
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {}
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>