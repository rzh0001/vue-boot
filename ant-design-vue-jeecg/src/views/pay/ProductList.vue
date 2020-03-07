<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="产品代码">
              <a-input placeholder="请输入产品代码" v-model="queryParam.productCode"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="产品名称">
              <a-input placeholder="请输入产品名称" v-model="queryParam.productName"></a-input>
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
          <a @click="handleEdit(record)">修改</a>

          <a-divider type="vertical" />
          <a @click="activeChannel(record)">关联通道</a>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <product-modal ref="modalForm" @ok="modalFormOk"></product-modal>
    <active-channel-modal ref="activeChannelModal"></active-channel-modal>
  </a-card>
</template>

<script>
  import ProductModal from './modules/ProductModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import ActiveChannelModal from './modules/ActiveChannelModal'
  export default {
    name: "ProductList",
    mixins:[JeecgListMixin],
    components: {
      ProductModal,
      ActiveChannelModal
    },
    data () {
      return {
        description: '产品表管理页面',
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
            title: '产品代码',
            align:"center",
            dataIndex: 'productCode'
           },
		   {
            title: '产品名称',
            align:"center",
            dataIndex: 'productName'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/product/product/list",
          delete: "/product/product/delete",
          deleteBatch: "/product/product/deleteBatch",
          exportXlsUrl: "product/product/exportXls",
          importExcelUrl: "product/product/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
      activeChannel: function(record) {
        this.$refs.activeChannelModal.relationChannel(record)
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>