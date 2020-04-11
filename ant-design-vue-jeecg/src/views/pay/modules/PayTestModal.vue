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
          label="产品选择">
          <select v-decorator="['productName', validatorRules.productName ]">
            <option v-for="option in products" v-bind:value="option.productCode">
              {{ option.productName}}
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
  import { httpAction,getAction } from '@/api/manage'
 import { Encrypt } from '@/utils/encryption/secret.js'
  import { MD5 } from '@/utils/encryption/secret.js'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "PayTestModal",
    data () {
      return {
        title:"操作",
        callBackUrl:"",
        products: [],
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
          productName:{rules: [{ required: true, message: '请选择产品' }]}
        },
        url: {
          add: "/api/create",
          channel: "/product/product/getAllProduct",
          getCallbackUrl:"/pay/channelEntity/getCallbackUrl"
        },
      }
    },
    created () {
    },
    mounted:function () {
      this.channel();
      this.getCallbackUrl();
    },
    methods: {
      channel(){
        let formData = [];
        formData.userName = "1";
        formData.memberType = "1";
        formData.agentUsername = "1";
        if(this.agentUsername===null){
          formData.agentUsername = "";
        }
        getAction(this.url.channel,formData).then((res)=>{
          if(res.success){
          this.products = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      getCallbackUrl(){
        httpAction(this.url.getCallbackUrl,null,'get').then((res)=>{
          if(res.success){
          this.callBackUrl = res.result;
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
            let callback = this.callBackUrl;
            let form = Object.assign(this.model, {"callbackUrl":callback,"outerOrderId":outerOrderId});
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