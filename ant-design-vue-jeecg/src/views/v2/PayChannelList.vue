<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="通道代码">
              <a-input placeholder="请输入通道代码" v-model="queryParam.channelCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="通道名称">
              <a-input placeholder="请输入通道名称" v-model="queryParam.channelName"></a-input>
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

      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
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
    <payChannel-modal ref="modalForm" @ok="modalFormOk"></payChannel-modal>
  </a-card>
</template>

<script>
  import PayChannelModal from './modules/PayChannelModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "PayChannelList",
    mixins:[JeecgListMixin],
    components: {
      PayChannelModal
    },
    data () {
      return {
        description: 'channel管理页面',
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
            title: '通道名称',
            align:"center",
            dataIndex: 'channelName'
          },
		   {
            title: '通道代码',
            align:"center",
            dataIndex: 'channelCode'
           },
		   {
            title: '通道网关',
            align:"center",
            dataIndex: 'channelGateway'
           },
		   {
            title: '通道IP白名单',
            align:"center",
            dataIndex: 'channelIp'
           },

		   {
            title: '通道默认费率',
            align:"center",
            dataIndex: 'channelRate'
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
          list: "/v2/payChannel/list",
          delete: "/v2/payChannel/delete",
          deleteBatch: "/v2/payChannel/deleteBatch",
          exportXlsUrl: "v2/payChannel/exportXls",
          importExcelUrl: "v2/payChannel/importExcel",
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