<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="设备名称">
              <a-input placeholder="请输入设备名称" v-model="queryParam.deviceName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="设备编码">
              <a-input placeholder="请输入设备编码" v-model="queryParam.deviceCode"></a-input>
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
     <!-- <a-button type="primary" icon="download" @click="handleExportXls('设备信息')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>-->
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
          <a @click="relationUser(record)">关联商户</a>

          <a-divider type="vertical" />
          <a @click="relationUser(record)">已关联商户</a>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <deviceInfoEntity-modal ref="modalForm" @ok="modalFormOk"></deviceInfoEntity-modal>
    <device-user-entity-modal ref="deviceUserForm"></device-user-entity-modal>
  </a-card>
</template>

<script>
  import DeviceInfoEntityModal from './modules/DeviceInfoEntityModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import DeviceUserEntityModal from "@views/df/modules/DeviceUserEntityModal";

  export default {
    name: "DeviceInfoEntityList",
    mixins:[JeecgListMixin],
    components: {
      DeviceUserEntityModal,
      DeviceInfoEntityModal
    },
    data () {
      return {
        description: '设备信息管理页面',
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
            title: '设备名称',
            align:"center",
            dataIndex: 'deviceName'
           },
		   {
            title: '设备编码',
            align:"center",
            dataIndex: 'deviceCode'
           },
		   {
            title: '秘钥',
            align:"center",
            dataIndex: 'apiKey'
           },
		   {
            title: '限额',
            align:"center",
            dataIndex: 'limitMoney'
           },
		   {
            title: '余额',
            align:"center",
            dataIndex: 'balance'
           },
		   {
            title: '分组编码',
            align:"center",
            dataIndex: 'groupingCode'
           },
		   {
            title: '状态',
            align:"center",
            dataIndex: 'status',
         customRender: function(text) {
           if (text == 1) {
             return '正常'
           }else if (text == 2) {
             return '禁用'
           } else {
             return text
           }
         }
           },
		   {
            title: '清零时间',
            align:"center",
            dataIndex: 'clearedTime'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/df/deviceInfoEntity/list",
          delete: "/df/deviceInfoEntity/delete",
          deleteBatch: "/df/deviceInfoEntity/deleteBatch",
          exportXlsUrl: "df/deviceInfoEntity/exportXls",
          importExcelUrl: "df/deviceInfoEntity/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
      relationUser(record){
        this.$refs.deviceUserForm.relationUser(record);
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>