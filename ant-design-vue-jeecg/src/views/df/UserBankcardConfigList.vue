<template>
  <a-card :bordered="false">
    <!-- 抽屉 -->
    <a-drawer
      title="用户收款银行卡配置列表"
      :width="screenWidth"
      @close="onClose"
      :visible="visible"
    >
      <!-- 抽屉内容的border -->
      <div
        :style="{
          padding:'10px',
          border: '1px solid #e9e9e9',
          background: '#fff',
        }">

        <div class="table-page-search-wrapper">
          <a-form layout="inline" :form="form">
            <a-row :gutter="10">
              <a-col :md="8" :sm="12">
                <a-form-item label="用户">
                  <a-input style="width: 120px;" placeholder="" v-model="queryParam.userName"></a-input>
                </a-form-item>
              </a-col>

              <a-col :md="7" :sm="24">
              <span style="float: left;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery">搜索</a-button>
                <a-button type="primary" @click="searchReset" style="margin-left: 8px">重置</a-button>
              </span>
              </a-col>
            </a-row>
<!--            <a-row>-->
<!--              <a-col :md="2" :sm="24">-->
<!--                <a-button style="margin-bottom: 10px" type="primary" @click="handleAdd">新增</a-button>-->
<!--              </a-col>-->
<!--            </a-row>-->
          </a-form>
        </div>
        <div>
          <a-table
            ref="table"
            rowKey="id"
            size="middle"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            @change="handleTableChange"
          >

          <span slot="action" slot-scope="text, record">
            <a v-if="record.status !== '1'" @click="handleAdd(record)">启用</a>
            <a-popconfirm title="确定取消吗?" v-if="record.status !== '0'"  @confirm="() => handleDelete(record.id)">
              <a>取消</a>
            </a-popconfirm>
          </span>

          </a-table>
        </div>
      </div>
    </a-drawer>
  </a-card>
</template>

<script>
  import pick from 'lodash.pick'
  import {filterObj} from '@/utils/util';
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import { postAction } from '../../api/manage'

  export default {
    name: "UserBankcardConfigList",
    mixins: [JeecgListMixin],
    components: {

    },
    data() {
      return {
        columns: [
          {
            title: '用户',
            align: "center",
            dataIndex: 'username',
          },
          {
            title: '是否启用',
            align: "center",
            dataIndex: 'status',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            scopedSlots: {customRender: 'action'},
          }
        ],
        queryParam: {
          dictId: "",
          dictName: "",
          itemText: "",
          delFlag: "1",
          status: [],
        },
        title: "操作",
        visible: false,
        screenWidth: 800,
        model: {},
        cardId: "",
        status: 1,
        labelCol: {
          xs: {span: 5},
          sm: {span: 5},
        },
        wrapperCol: {
          xs: {span: 12},
          sm: {span: 12},
        },
        form: this.$form.createForm(this),
        validatorRules: {
          itemText: {rules: [{required: true, message: '请输入名称!'}]},
          itemValue: {rules: [{required: true, message: '请输入数据值!'}]},
        },
        url: {
          list: "/df/userBankcardConfig/list",
          add: "/df/userBankcardConfig/add",
          delete: "/df/userBankcardConfig/delete",
        },
      }
    },
    created() {
      // 当页面初始化时,根据屏幕大小来给抽屉设置宽度
      this.resetScreenSize();
    },
    methods: {
      add(dictId) {
        this.dictId = dictId;
        this.edit({});
      },
      edit(record) {
        console.log('cardid is '+ record.id)

        if (record.id) {
          console.log('cardid is '+ record.id)
          this.cardId = record.id;
        }
        this.queryParam = {}
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.model.cardId = this.cardId;
        // this.model.status = this.status;
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'itemText', 'itemValue'))
        });
        // 当其它模块调用该模块时,调用此方法加载字典数据
        this.loadData();
      },

      getQueryParams() {
        var param = Object.assign({}, this.queryParam);
        param.cardId = this.cardId;
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },


      handleAdd(record) {
        postAction(this.url.add, {userId: record.userId, userName: record.username, cardId: this.cardId}).then(res => {
          if (res.success) {
            this.$message.success(res.message);
            this.loadData();
          } else {
            this.$message.warning(res.message);
          }
        })


      },
      showDrawer() {
        this.visible = true
      },
      onClose() {
        this.visible = false
        this.form.resetFields();
        this.dataSource = [];
      },
      // 抽屉的宽度随着屏幕大小来改变
      resetScreenSize() {
        let screenWidth = document.body.clientWidth;
        if (screenWidth < 600) {
          this.screenWidth = screenWidth;
        } else {
          this.screenWidth = 600;
        }
      },
    }
  }
</script>
<style scoped>
</style>