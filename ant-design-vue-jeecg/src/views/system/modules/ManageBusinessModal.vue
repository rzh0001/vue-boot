<template>
  <div>
    <a-modal
      :title="title"
      :width="800"
      :visible="manage"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭">
    <form :autoFormCreate="(form) => this.form = form" >
      <a-table
        :columns="columns"
        :dataSource="data"
        :pagination="false"
      >
        <template v-for="(col, i) in [ 'businessCode','businessApiKey','businessActiveStatus']" :slot="col" slot-scope="text, record, index">
          <a-input
            :key="col"
            v-if="record.editable"
            style="margin: -5px 0"
            :value="text"
            :placeholder="columns[i+4].title"
            @change="e => handleChange(e.target.value, record.key, col)"
          />
          <template v-else>{{ text }}</template>
        </template>
        <template slot="operation" slot-scope="text, record, index">
          <template v-if="record.editable">
              <span v-if="record.isNew">
                <a @click="saveRow(record.key)">添加</a>
                <a-divider type="vertical" />
                <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
                  <a>删除</a>
                </a-popconfirm>
              </span>
            <span v-else>
                <a @click="saveRow(record.key)">保存</a>
                <a-divider type="vertical" />
                <a @click="cancel(record.key)">取消</a>
              </span>
          </template>
          <span v-else>
              <a @click="toggle(record.key)">编辑</a>
             <!-- <a-divider type="vertical" />
              <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
                <a>删除</a>
              </a-popconfirm>-->
            <a-divider type="vertical" />
              <a-popconfirm title="确认激活码？" @confirm="active(record.key)">
                <a>激活</a>
              </a-popconfirm>
            <a-divider type="vertical" />
              <a-popconfirm title="确认失效码？" @confirm="unActive(record.key)">
                <a>失效</a>
              </a-popconfirm>
            </span>
        </template>
      </a-table>
    </form>
    </a-modal>
  </div>

</template>

<script>
  import {getAction,httpAction} from '@/api/manage'
  import JCheckbox from '@/components/jeecg/JCheckbox'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
  import STable from '@/components/table/'
  export default {
    name: "ManageBusinessModal",
    components: {
      JCheckbox,
      JMultiSelectTag,
      STable
    },
    data () {
      return {
        title:"管理渠道",
        description: '高级表单常见于一次性输入和提交大批量数据的场景。',
        loading: false,
        manage:false,
        userName:"",
        // table
        columns: [
          {
            title: '商户名称',
            dataIndex: 'userName',
            key: 'userName',
            width: '10%',
            scopedSlots: { customRender: 'userName' },

          },
          {
            title: '产品名称',
            dataIndex: 'productCode',
            key: 'productCode',
            width: '10%',
            scopedSlots: { customRender: 'productCode' }
          },
          {
            title: '通道名称',
            dataIndex: 'channelCode',
            key: 'channelCode',
            width: '10%',
            scopedSlots: { customRender: 'channelCode' }
          },
          {
            title: '余额',
            dataIndex: 'balance',
            key: 'balance',
            width: '10%',
            scopedSlots: { customRender: 'balance' }
          },
          {
            title: '子账号名称',
            dataIndex: 'businessCode',
            key: 'businessCode',
            width: '10%',
            scopedSlots: { customRender: 'businessCode' }
          },
          {
            title: '秘钥',
            dataIndex: 'businessApiKey',
            key: 'businessApiKey',
            width: '20%',
            scopedSlots: { customRender: 'businessApiKey' }
          },
          {
            title: '状态',
            dataIndex: 'businessActiveStatus',
            key: 'businessActiveStatus',
            width: '10%',
            customRender: function(text) {
              if (text == '0') {
                return <a-tag color="red">未激活</a-tag>
              } else if (text == '1') {
                return  <a-tag color="cyan">已激活</a-tag>
              } else{
                return text
              }
            }
          },
          {
            title: '操作',
            key: 'action',
            scopedSlots: { customRender: 'operation' }
          }
        ],
        data: [],
        url:{
          getBusinessByAgentName:"/v2/payBusiness/getBusinessByAgentName",
          updateBusiness:"/v2/payBusiness/updateBusiness",
          activeBusinessStatus:"/v2/payBusiness/activeBusinessStatus",
          unActiveBusinessStatus:"/v2/payBusiness/unActiveBusinessStatus",
          deleteBusiness:"/v2/payBusiness/deleteBusiness",
        }
      }
    },
    mounted:function () {
    },
    methods: {
      getBusinessByAgentName(){
        let formData = [];
        formData.userName = this.userName;
        getAction(this.url.getBusinessByAgentName,formData).then((res)=>{
          if(res.success){
            this.data=res.result;
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        })
      },
      close() {
        this.$emit('close');
        this.manage = false;
        this.userName = '';
      },
      handleOk(){
        this.$emit('close');
        this.manage = false;
        this.userName = '';
      },
      handleCancel() {
        this.close()
      },
      manageBusiness(record){
        if(record.memberType != '1'){
          alert("无权限")
          return;
        }
        this.manage=true;
        this.userName=record.username;
        this.getBusinessByAgentName();
      },
      handleSubmit (e) {
        e.preventDefault()
      },
      newMember () {
        this.data.push({
          key: '-1',
          name: '',
          workId: '',
          department: '',
          editable: true,
          isNew: true
        })
      },
      remove (key) {
        let target = this.data.filter(item => item.key === key)[0]
        httpAction(this.url.deleteBusiness,target,"post").then((res)=>{
          if(res.success){
            this.$message.success(res.message);
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
          that.close();
        })
      },
      active (key){
        let target = this.data.filter(item => item.key === key)[0]
        httpAction(this.url.activeBusinessStatus,target,"post").then((res)=>{
          if(res.success){
            this.$message.success(res.message);
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
          that.close();
        })
      },
      unActive(key){
        let target = this.data.filter(item => item.key === key)[0]
        httpAction(this.url.unActiveBusinessStatus,target,"post").then((res)=>{
          if(res.success){
            this.$message.success(res.message);
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
          that.close();
        })
      },
      saveRow (key) {
        let target = this.data.filter(item => item.key === key)[0]
        httpAction(this.url.updateBusiness,target,"post").then((res)=>{
          if(res.success){
            this.$message.success(res.message);
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
          that.close();
        })
      },
      toggle (key) {
        let target = this.data.filter(item => item.key === key)[0]
        target.editable = !target.editable
      },
      getRowByKey (key, newData) {
        const data = this.data
        return (newData || data).filter(item => item.key === key)[0]
      },
      cancel (key) {
        let target = this.data.filter(item => item.key === key)[0]
        target.editable = false
      },
      handleChange (value, key, column) {
        const newData = [...this.data]
        const target = newData.filter(item => key === item.key)[0]
        if (target) {
          target[column] = value
          this.data = newData
        }
      },

      // 最终全页面提交
      validate () {
        this.$refs.repository.form.validateFields((err, values) => {
          if (!err) {
            this.$notification['error']({
              message: 'Received values of form:',
              description: values
            })
          }
        })
        this.$refs.task.form.validateFields((err, values) => {
          if (!err) {
            this.$notification['error']({
              message: 'Received values of form:',
              description: values
            })
          }
        })
      }
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px!important;
  }
</style>