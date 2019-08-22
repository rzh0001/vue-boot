<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="代理">
              <a-input placeholder="请输入代理账号" v-model="queryParam.userName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
          <a-form-item label="挂码账号">
            <a-input placeholder="请输入挂码账号" v-model="queryParam.businessCode"></a-input>
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
    <userBusinessEntity-modal ref="modalForm" @ok="modalFormOk"></userBusinessEntity-modal>
  </a-card>
</template>

<script>
  import UserBusinessEntityModal from './modules/UserBusinessEntityModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "UserBusinessEntityList",
    mixins:[JeecgListMixin],
    components: {
      UserBusinessEntityModal
    },
    data () {
      return {
        description: '用户关联商户管理页面',
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
            title: '代理账号',
            align:"center",
            dataIndex: 'userName'
           },
		   {
            title: '挂码账号',
            align:"center",
            dataIndex: 'businessCode'
           },
          {
            title: '秘钥',
            align:"center",
            dataIndex: 'apiKey'
          },
          {
            title: '通道',
            align: "center",
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
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/pay/userBusinessEntity/list",
          delete: "/pay/userBusinessEntity/delete",
          deleteBatch: "/pay/userBusinessEntity/deleteBatch",
          exportXlsUrl: "pay/userBusinessEntity/exportXls",
          importExcelUrl: "pay/userBusinessEntity/importExcel",
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