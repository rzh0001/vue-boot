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
            <template v-for="(col, i) in ['userName', 'businessCode', 'apiKey','channelCode']" :slot="col" slot-scope="text, record, index">
              <a-tooltip  title="必填项" :defaultVisible="false" overlayStyle="{ color: 'red' }">
                <a-input :key="col" v-if="record.editable" style="margin: -5px 0"  :value="text" :placeholder="columns[i].title" @change="e => handlerRowChange(e.target.value, record.channelCode, col)"/>
                <template v-else>{{ text }}</template>
              </a-tooltip>
            </template>
            <template slot="operation" slot-scope="text, record, index">
              <template v-if="record.editable">
                <span>
                    <a @click="saveRow(record.channelCode)">保存</a>
                    <a-divider type="vertical"/>
                    <a @click="cancelEditRow(record.channelCode)">取消</a>
                </span>
              </template>
              <span v-else>
                <!--<a @click="editRow(record.channelCode)">编辑</a>
                <a-divider type="vertical" />-->
                <a-popconfirm title="是否要删除此通道？" @confirm="removeRow(record)"><a>删除</a></a-popconfirm>
                </span>
            </template>
          </a-table>
        </a-form>
      </a-spin>
    </a-modal>

    <a-modal
      :title="title"
      :width="500"
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
            label="挂码账号">
            <a-input placeholder="挂码账号" style="width:200px;" v-decorator="['businessCode', validatorRules.businessCode]"/>
          </a-form-item>
          <a-form-item
            label="秘钥">
            <a-input placeholder="秘钥" style="width:200px;" v-decorator="['apiKey', validatorRules.apiKey]" />
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
    name: "UserBusinessModal",
    data() {
      return {
        userName: '',
        title: "挂马详情",
        title4add:"添加",
        visible: false,
        channels: [],
        visible4Add:false,
        model: {},
        columns: [
          {
            title: '商户',
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
              } else if (text == 'wechat_bank') {
                return '微信转卡'
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
          channelCode:{rules: [{ required: true, message: '请选择通道!' }]},
          businessCode:{rules: [{ required: true, message: '请输入挂码账号!' }]}
        },
        url: {
          queryUserBusiness: "/pay/userBusinessEntity/queryUserBusiness",
          channel: "/pay/channelEntity/channel",
          deleteUserBusiness:"/pay/userBusinessEntity/deleteUserBusiness",
          addUserBusiness:"/pay/userBusinessEntity/add",
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
        getAction(this.url.queryUserBusiness,params).then((res)=>{
          if(res.success){
          this.data = res.result;
        }else{
        }
      })
      },
      addUserBusiness:function(record){
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
          httpAction(this.url.addUserBusiness,formData,"post").then((res)=>{
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
        console.log(record);
        const that = this;
        let userName = record.userName;
        let channelCode = record.channelCode;
        let businessCode = record.businessCode;
        var params = {
          userName:userName,
          channelCode:channelCode,
          businessCode:businessCode
        };
        httpAction(this.url.deleteUserBusiness,params,"post").then((res)=>{
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
      editRow(channelCode){
        let target = this.data.filter(item => item.channelCode === channelCode)[0];
        target.editable = !target.editable
      },
      handlerRowChange (value, key, column) {
        const newData = [...this.data];
        const target = this.data.filter(item => key === item.channelCode)[0];
        if (target) {
          target[column] = value;
          this.data = newData;
        }
      },
      saveRow (key) {
        let target = this.data.filter(item => item.channelCode === key)[0]
        target.editable = false
        target.isNew = false
      },
      cancelEditRow (key) {
        let target = this.data.filter(item => item.channelCode === key)[0]
        target.editable = false
      },
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px!important;
  }
</style>