<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="userId">
              <a-input placeholder="请输入userId" v-model="queryParam.userId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="用户名">
              <a-input placeholder="请输入用户名" v-model="queryParam.userName"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="流水类型 1-手续费收入 2-提现 3-冲正">
              <a-input placeholder="请输入流水类型 1-手续费收入 2-提现 3-冲正" v-model="queryParam.type"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="金额">
              <a-input placeholder="请输入金额" v-model="queryParam.amount"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="订单号">
              <a-input placeholder="请输入订单号" v-model="queryParam.orderId"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('用户收入流水详情')">导出</a-button>
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
    <userAmountDetail-modal ref="modalForm" @ok="modalFormOk"></userAmountDetail-modal>
  </a-card>
</template>

<script>
  import UserAmountDetailModal from './modules/UserAmountDetailModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "UserAmountDetailList",
    mixins:[JeecgListMixin],
    components: {
      UserAmountDetailModal
    },
    data () {
      return {
        description: '用户收入流水详情管理页面',
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
            title: 'userId',
            align:"center",
            dataIndex: 'userId'
           },
		   {
            title: '用户名',
            align:"center",
            dataIndex: 'userName'
           },
		   {
            title: '流水类型 1-手续费收入 2-提现 3-冲正',
            align:"center",
            dataIndex: 'type'
           },
		   {
            title: '金额',
            align:"center",
            dataIndex: 'amount'
           },
		   {
            title: '订单号',
            align:"center",
            dataIndex: 'orderId'
           },
		   {
            title: '费率',
            align:"center",
            dataIndex: 'userRate'
           },
		   {
            title: '代理ID',
            align:"center",
            dataIndex: 'agentId'
           },
		   {
            title: '代理用户名',
            align:"center",
            dataIndex: 'agentUsername'
           },
		   {
            title: '代理姓名',
            align:"center",
            dataIndex: 'agentRealname'
           },
		   {
            title: '介绍人id',
            align:"center",
            dataIndex: 'salesmanId'
           },
		   {
            title: '介绍人用户名',
            align:"center",
            dataIndex: 'salesmanUsername'
           },
		   {
            title: '介绍人姓名',
            align:"center",
            dataIndex: 'salesmanRealname'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/pay/userAmountDetail/list",
          delete: "/pay/userAmountDetail/delete",
          deleteBatch: "/pay/userAmountDetail/deleteBatch",
          exportXlsUrl: "pay/userAmountDetail/exportXls",
          importExcelUrl: "pay/userAmountDetail/importExcel",
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