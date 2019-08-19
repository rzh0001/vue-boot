<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="订单号">
              <a-input placeholder="请输入订单号" v-model="queryParam.orderId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="用户名">
              <a-input placeholder="请输入用户名" v-model="queryParam.userName"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="高级代理">
              <a-input placeholder="请输入高级代理" v-model="queryParam.agentName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="介绍人名称">
              <a-input placeholder="请输入介绍人名称" v-model="queryParam.introducerName"></a-input>
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

    <!-- table区域-begin -->
    <div>

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
    <rateLogEntity-modal ref="modalForm" @ok="modalFormOk"></rateLogEntity-modal>
  </a-card>
</template>

<script>
  import RateLogEntityModal from './modules/RateLogEntityModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "RateLogEntityList",
    mixins:[JeecgListMixin],
    components: {
      RateLogEntityModal
    },
    data () {
      return {
        description: '手续费收取详情管理页面',
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
            title: '订单号',
            align:"center",
            dataIndex: 'orderId'
           },
		   {
            title: '用户名',
            align:"center",
            dataIndex: 'userName'
           },
		   {
            title: '高级代理',
            align:"center",
            dataIndex: 'agentName'
           },
		   {
            title: '介绍人名称',
            align:"center",
            dataIndex: 'introducerName'
           },
		   {
            title: '费率',
            align:"center",
            dataIndex: 'userRate'
           },
		   {
            title: '申请金额',
            align:"center",
            dataIndex: 'submitamount'
           },
		   {
            title: '手续费',
            align:"center",
            dataIndex: 'poundage'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/pay/rateLogEntity/list",
          delete: "/pay/rateLogEntity/delete",
          deleteBatch: "/pay/rateLogEntity/deleteBatch",
          exportXlsUrl: "pay/rateLogEntity/exportXls",
          importExcelUrl: "pay/rateLogEntity/importExcel",
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