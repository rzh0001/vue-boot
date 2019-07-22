<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="四方系统订单号">
              <a-input placeholder="请输入四方系统订单号" v-model="queryParam.orderId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="外部订单号">
              <a-input placeholder="请输入外部订单号" v-model="queryParam.outerOrderId"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="商户编号">
              <a-input placeholder="请输入商户编号" v-model="queryParam.businessCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="商户名称">
              <a-input placeholder="请输入商户名称" v-model="queryParam.businessName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="申请金额">
              <a-input placeholder="请输入申请金额" v-model="queryParam.submitAmount"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('订单信息')">导出</a-button>
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
    <orderInfoEntity-modal ref="modalForm" @ok="modalFormOk"></orderInfoEntity-modal>
  </a-card>
</template>

<script>
  import OrderInfoEntityModal from './modules/OrderInfoEntityModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "OrderInfoEntityList",
    mixins:[JeecgListMixin],
    components: {
      OrderInfoEntityModal
    },
    data () {
      return {
        description: '订单信息管理页面',
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
            title: '四方系统订单号',
            align:"center",
            dataIndex: 'orderId'
           },
		   {
            title: '外部订单号',
            align:"center",
            dataIndex: 'outerOrderId'
           },
		   {
            title: '商户编号',
            align:"center",
            dataIndex: 'businessCode'
           },
		   {
            title: '商户名称',
            align:"center",
            dataIndex: 'businessName'
           },
		   {
            title: '申请金额',
            align:"center",
            dataIndex: 'submitAmount'
           },
		   {
            title: '手续费',
            align:"center",
            dataIndex: 'poundage'
           },
		   {
            title: '实际金额',
            align:"center",
            dataIndex: 'actual-amount'
           },
		   {
            title: '状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回',
            align:"center",
            dataIndex: 'status'
           },
		   {
            title: '支付通道',
            align:"center",
            dataIndex: 'payType'
           },
		   {
            title: '回调地址',
            align:"center",
            dataIndex: 'callbackUrl'
           },
		   {
            title: '可用金额，即可提现额度',
            align:"center",
            dataIndex: 'availableAmount'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/pay/orderInfoEntity/list",
          delete: "/pay/orderInfoEntity/delete",
          deleteBatch: "/pay/orderInfoEntity/deleteBatch",
          exportXlsUrl: "pay/orderInfoEntity/exportXls",
          importExcelUrl: "pay/orderInfoEntity/importExcel",
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