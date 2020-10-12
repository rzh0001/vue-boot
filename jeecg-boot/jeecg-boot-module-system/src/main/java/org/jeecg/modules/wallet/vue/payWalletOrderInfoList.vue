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
            <a-form-item label="钱包地址">
              <a-input placeholder="请输入钱包地址" v-model="queryParam.walletUrl"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="userName">
              <a-input placeholder="请输入userName" v-model="queryParam.userName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="代理">
              <a-input placeholder="请输入代理" v-model="queryParam.agentName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="介绍人帐号">
              <a-input placeholder="请输入介绍人帐号" v-model="queryParam.salesmanUsername"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('钱包订单信息')">导出</a-button>
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
    <payWalletOrderInfo-modal ref="modalForm" @ok="modalFormOk"></payWalletOrderInfo-modal>
  </a-card>
</template>

<script>
  import payWalletOrderInfoModal from './modules/payWalletOrderInfoModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "payWalletOrderInfoList",
    mixins:[JeecgListMixin],
    components: {
      payWalletOrderInfoModal
    },
    data () {
      return {
        description: '钱包订单信息管理页面',
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
            title: '钱包地址',
            align:"center",
            dataIndex: 'walletUrl'
           },
		   {
            title: 'userName',
            align:"center",
            dataIndex: 'userName'
           },
		   {
            title: '代理',
            align:"center",
            dataIndex: 'agentName'
           },
		   {
            title: '介绍人帐号',
            align:"center",
            dataIndex: 'salesmanUsername'
           },
		   {
            title: '申请金额，单位为分',
            align:"center",
            dataIndex: 'amount'
           },
		   {
            title: '币种类型',
            align:"center",
            dataIndex: 'coinType'
           },
		   {
            title: '币种数量',
            align:"center",
            dataIndex: 'coinQuantity'
           },
		   {
            title: '回调地址',
            align:"center",
            dataIndex: 'callbackUrl'
           },
		   {
            title: 'endTime',
            align:"center",
            dataIndex: 'endTime'
           },
		   {
            title: '状态：-1:无效  0:未支付 1:成功，未返回 2:成功，已返回',
            align:"center",
            dataIndex: 'status'
           },
		   {
            title: '是否补单 1:是 2：否',
            align:"center",
            dataIndex: 'reissue'
           },
		   {
            title: 'ip',
            align:"center",
            dataIndex: 'ip'
           },
		   {
            title: '订单备注',
            align:"center",
            dataIndex: 'remark'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/wallet/payWalletOrderInfo/list",
          delete: "/wallet/payWalletOrderInfo/delete",
          deleteBatch: "/wallet/payWalletOrderInfo/deleteBatch",
          exportXlsUrl: "wallet/payWalletOrderInfo/exportXls",
          importExcelUrl: "wallet/payWalletOrderInfo/importExcel",
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