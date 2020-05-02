<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="代理帐号">
              <a-input placeholder="" v-model="queryParam.agentUsername"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="开始时间">
              <j-date v-model="queryParam.createTime_begin" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="结束时间">
              <j-date v-model="queryParam.createTime_end" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"/>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
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

        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'

  export default {
    name: "TransactionVolumeList",
    mixins:[JeecgListMixin],
    components: {
      JDate,

    },
    data () {
      return {
        description: 'd管理页面',
        // 表头
        columns: [
		   {
            title: '代理帐号',
            align:"center",
            dataIndex: 'agentUsername'
           },
		   {
            title: '交易量',
            align:"center",
            dataIndex: 'amount'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/pay/transactionVolume/list",
          delete: "/pay/transactionVolume/delete",
          deleteBatch: "/pay/transactionVolume/deleteBatch",
          exportXlsUrl: "pay/transactionVolume/exportXls",
          importExcelUrl: "pay/transactionVolume/importExcel",
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