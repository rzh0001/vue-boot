<template>
  <div>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleCancel"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
         <a-table :columns="columns" :dataSource="data" :pagination="false" size="middle">
              <template slot="operation" slot-scope="text, record, index">
                <template v-if="record.editable">
                </template>
                <span v-else>
                  <a-popconfirm title="是否要删除此通道？" @confirm="removeRow(record)"><a>删除</a></a-popconfirm>
                </span>
              </template>
            </a-table>
      </a-form>
    </a-spin>
  </a-modal>

  <a-modal
    :title="title4add"
    :width="800"
    :visible="visible4Add"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="close4Add"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          label="商户">
          <a-input placeholder="商户" style="width:200px;" readonly="true" v-model="userName"/>
        </a-form-item>
        <a-form-item
          label="通道">
          <select v-decorator="['channelCode', validatorRules.channelCode ]">
            <option v-for="option in channels" v-bind:value="option.channelCode">
              {{ option.channelName}}
            </option>
          </select>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
  </div>
</template>

<script>
  import {getAction,httpAction} from '@/api/manage'
  export default {
    name: "UserChannelModal",
    data() {
      return {
        userName: '',
        title: "通道详细",
        title4add:"添加",
        visible: false,
        channels: [],
        visible4Add:false,
        model: {},
        columns: [
          {
            title: '商户',
            dataIndex: 'userName',
            key: 'userName',
            width: '20%',
            scopedSlots: {customRender: 'userName'}
          },

          {
            title: '通道',
            dataIndex: 'channelCode',
            key: 'channelCode',
            width: '40%',
            scopedSlots: {customRender: 'channelCode'},
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
            key: 'action',
            scopedSlots: {customRender: 'operation'}
          }
        ],
        data:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          channelCode:{rules: [{ required: true, message: '请选择通道!' }]}
        },
        url: {
          queryChannelByUserName: "/pay/userChannelEntity/queryChannelByUserName",
          channel: "/pay/channelEntity/channel",
          deleteUserChannel:"/pay/userChannelEntity/deleteUserChannel",
          addUserChannel:"/pay/userChannelEntity/add",
        },
      }
    },
    mounted:function () {
      this.channel();
    },
    methods: {
      channel(){
        httpAction(this.url.channel,null,'get').then((res)=>{
          if(res.success){
          this.channels = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      detail:function(record) {
        this.visible = true;
        var params = {username:record.username};//查询条件
        getAction(this.url.queryChannelByUserName,params).then((res)=>{
          if(res.success){
          this.data = res.result;
        }else{
        }
      })
      },
      addChannel:function(record){
        this.visible4Add=true;
        console.log(record);
        this.userName = record.username;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
          console.log(values);
          that.confirmLoading = true;
          let formData = Object.assign(this.model, values);
          formData.userName=this.userName;
          console.log(formData);
          //时间格式化
          httpAction(this.url.addUserChannel,formData,"post").then((res)=>{
            if(res.success){
            that.$message.success(res.message);
            that.$emit('ok');
          }else{
            that.$message.warning(res.message);
          }
        }).finally(() => {
            that.confirmLoading = false;
          that.close4Add();
        })
        }
      })
      },
      close() {
        this.$emit('close');
        this.visible = false;
      },
      close4Add(){
        this.$emit('close');
        this.visible4Add = false;
      },
      handleCancel() {
        this.close()
      },
      //删除通道
      removeRow (record) {
        const that = this;
       let userName = record.userName;
       let channelCode = record.channelCode;
        var params = {
          userName:userName,
          channelCode:channelCode
        };
        httpAction(this.url.deleteUserChannel,params,"post").then((res)=>{
          if(res.success){
          that.$message.success(res.message);
          that.close();
        }else{
          that.$message.warning(res.message);
        }
      }).finally(() => {
          that.confirmLoading = false;
          that.close();
      })
      },
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px!important;
  }
</style>