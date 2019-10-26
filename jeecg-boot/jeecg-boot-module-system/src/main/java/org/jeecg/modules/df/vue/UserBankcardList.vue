<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="用户ID">
              <a-input placeholder="请输入用户ID" v-model="queryParam.userId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="用户登录账号">
              <a-input placeholder="请输入用户登录账号" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="账户类型(1-对私;2-对公)">
              <a-input placeholder="请输入账户类型(1-对私;2-对公)" v-model="queryParam.accountType"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="账户名">
              <a-input placeholder="请输入账户名" v-model="queryParam.accountName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="卡号">
              <a-input placeholder="请输入卡号" v-model="queryParam.cardNumber"></a-input>
            </a-form-item>
          </a-col>
          </template>
          <a-col :md="6" :sm="8" >
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
      <a-button type="primary" icon="download" @click="handleExportXls('代付平台用户银行卡')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
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
    <userBankcard-modal ref="modalForm" @ok="modalFormOk"></userBankcard-modal>
  </a-card>
</template>

<script>
  import UserBankcardModal from './modules/UserBankcardModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "UserBankcardList",
    mixins:[JeecgListMixin],
    components: {
      UserBankcardModal
    },
    data () {
      return {
        description: '代付平台用户银行卡管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
           },
		   {
            title: '用户ID',
            align:"center",
            dataIndex: 'userId'
           },
		   {
            title: '用户登录账号',
            align:"center",
            dataIndex: 'username'
           },
		   {
            title: '账户类型(1-对私;2-对公)',
            align:"center",
            dataIndex: 'accountType'
           },
		   {
            title: '账户名',
            align:"center",
            dataIndex: 'accountName'
           },
		   {
            title: '卡号',
            align:"center",
            dataIndex: 'cardNumber'
           },
		   {
            title: '银行名称',
            align:"center",
            dataIndex: 'bankName'
           },
		   {
            title: '开户行全称',
            align:"center",
            dataIndex: 'branchName'
           },
		   {
            title: '备注',
            align:"center",
            dataIndex: 'remark'
           },
		   {
            title: '开关(0-关闭;1-开启)',
            align:"center",
            dataIndex: 'isOpen'
           },
		   {
            title: '默认卡(0-否;1-是)',
            align:"center",
            dataIndex: 'isDefault'
           },
		   {
            title: '删除状态(0-正常;1-已删除)',
            align:"center",
            dataIndex: 'delFlag'
           },
		   {
            title: '乐观锁',
            align:"center",
            dataIndex: 'version'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/df/userBankcard/list",
          delete: "/df/userBankcard/delete",
          deleteBatch: "/df/userBankcard/deleteBatch",
          exportXlsUrl: "df/userBankcard/exportXls",
          importExcelUrl: "df/userBankcard/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
     
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>