<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="用户名">
              <a-input placeholder="请输入用户名" v-model="queryParam.userName"></a-input>
            </a-form-item>
          </a-col>
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
    <userRateEntity-modal ref="modalForm" @ok="modalFormOk"></userRateEntity-modal>
  </a-card>
</template>

<script>
  import UserRateEntityModal from './modules/UserRateEntityModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "UserRateEntityList",
    mixins:[JeecgListMixin],
    components: {
      UserRateEntityModal
    },
    data () {
      return {
        description: '费率管理页面',
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
            title: '用户名',
            align:"center",
            dataIndex: 'userName'
           },
          {
            title: '通道',
            align:"center",
            dataIndex: 'channelCode',
            key: 'channelCode',
            customRender: function (text) {
              if (text == 'ysf') {
                return '云闪付'
              } else if (text == 'ali_bank') {
                return '支付宝转卡'
              } else if (text == 'ali_zz') {
                return '支付宝转账'
              }else if (text == 'nxys_wx') {
                return '农信易扫微信'
              }else if (text == 'nxys_alipay') {
                return '农信易扫支付宝'
              } else {
                return text
              }
            }
          },
		   {
            title: '费率',
            align:"center",
            dataIndex: 'userRate'
           },
		   {
            title: '高级代理名称',
            align:"center",
            dataIndex: 'agentId'
           },
          {
            title: '被介绍人名称',
            align:"center",
            dataIndex: 'beIntroducerName'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/pay/userRateEntity/list",
          delete: "/pay/userRateEntity/delete",
          deleteBatch: "/pay/userRateEntity/deleteBatch",
          exportXlsUrl: "pay/userRateEntity/exportXls",
          importExcelUrl: "pay/userRateEntity/importExcel",
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