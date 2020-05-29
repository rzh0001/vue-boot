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
        <a-col :md="6" :sm="8">
            <a-form-item label="产品名称">
              <a-input placeholder="请输入产品名称" v-model="queryParam.productName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
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

            <a-menu-item>
                <a @click="relatedChannels(record)">关联通道信息</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <payProduct-modal ref="modalForm" @ok="modalFormOk"></payProduct-modal>
    <related-channels-modal ref="relatedChannelsModal"></related-channels-modal>
  </a-card>

</template>

<script>
  import PayProductModal from './modules/PayProductModal'
  import RelatedChannelsModal from './modules/RelatedChannelsModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "PayProductList",
    mixins:[JeecgListMixin],
    components: {
      PayProductModal,
      RelatedChannelsModal
    },
    data () {
      return {
        description: '产品管理页面',
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
            title: '状态',
            align:"center",
            dataIndex: 'status',
         customRender: function(text) {
           if (text == 0) {
             return <a-tag color="red">关闭</a-tag>
           } else if (text == 1) {
             return <a-tag color="cyan">开启</a-tag>
           } else {
             return text
           }
         }
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/v2/payProduct/list",
          delete: "/v2/payProduct/delete",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
      relatedChannels: function(record) {
        this.$refs.relatedChannelsModal.relatedProductChannels(record)
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>