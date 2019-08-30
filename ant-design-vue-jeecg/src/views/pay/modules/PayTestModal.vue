<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="金额">
          <a-input placeholder="申请金额" v-decorator="['submitAmount', validatorRules.submitAmount]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="用户名">
          <a-input placeholder="用户名" v-decorator="['username', validatorRules.username]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="通道代码">
          <select v-decorator="['payType', validatorRules.payType ]">
            <option v-for="option in channels" v-bind:value="option.channelCode">
              {{ option.channelName}}
            </option>
          </select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="apikey">
          <a-input placeholder="apikey" v-decorator="['apikey', validatorRules.apikey]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
 import { Encrypt } from '@/utils/encryption/secret.js'
  import { MD5 } from '@/utils/encryption/secret.js'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "PayTestModal",
    data () {
      return {
        title:"操作",
        channels: [],
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          submitAmount:{rules: [{ required: true, message: '请输入金额!' }]},
          username:{rules: [{ required: true, message: '请输入用户名!' }]},
          payType:{rules: [{ required: true, message: '请输入通道代码' }]}
        },
        url: {
          add: "/api/create",
          channel: "/pay/channelEntity/channel"
        },
      }
    },
    created () {
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
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'amount','channelCode','username','delFlag','createUser','updateUser'))
		  //时间格式化
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = this.url.add;
            let method =  'post';
            let formData = Object.assign(this.model, values);
            let outerOrderId = Date.parse(new Date())+'abc';
            let form = Object.assign(this.model, {"callbackUrl":"http://www.jcokpay.com/jeecg-boot/api/testCallBack","outerOrderId":outerOrderId});
            let data = JSON.stringify(form);
          var jsondata =  JSON.parse(data);
            let key = jsondata.apikey;
          var timestamp = Date.parse(new Date());
          var sign = MD5(this.model.username+timestamp+Encrypt(data,key)+key);
          var jsonObj = {
            "data":Encrypt(data,key),
            "username":this.model.username,
            "timestamp":timestamp,
            //username+timestamp+data+apikey
            "sign": sign
          };
          console.log(jsonObj);
          console.log(httpurl);
            httpAction(httpurl,jsonObj,method).then((res)=>{
              if(res.code == '0'){
                that.$message.success(res.msg);
                if(res.url != null){
                  window.open(res.url);
                  that.$emit('ok');
                }
              }else{
                that.$message.warning(res.msg);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>