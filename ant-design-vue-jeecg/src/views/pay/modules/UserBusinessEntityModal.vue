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
          label="代理账号">
          <a-input placeholder="请输入代理账号" v-decorator="['userName', validatorRules.userName]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="挂码账号">
          <a-input placeholder="挂码账号" v-decorator="['businessCode', validatorRules.businessCode]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="秘钥">
          <a-input placeholder="秘钥" v-decorator="['apiKey', validatorRules.apiKey]" />
        </a-form-item>
        <!--<a-form-item-->
          <!--:labelCol="labelCol"-->
          <!--:wrapperCol="wrapperCol"-->
          <!--label="通道">-->
          <!--<select v-decorator="['channelCode', validatorRules.channelCode ]">-->
            <!--<option v-for="option in channels" v-bind:value="option.channelCode">-->
              <!--{{ option.channelName}}-->
            <!--</option>-->
          <!--</select>-->
        <!--</a-form-item>-->

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "UserBusinessEntityModal",
    data () {
      return {
        title:"操作",
        selected:'',
        products: [],
        visible: false,
        model: {},
        channels: [],
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
          userName:{rules: [{ required: true, message: '请输入代理!' }]},
          businessCode:{rules: [{ required: true, message: '请输入挂码账号!' }]},
          channelCode:{rules: [{ required: true, message: '请选择通道!' }]}
        },
        url: {
          add: "/pay/userBusinessEntity/add",
          edit: "/pay/userBusinessEntity/edit",
          channel: "/pay/channelEntity/channel",
          getAllProduct: "/product/product/getAllProduct",
          getUserProductChannel: "/productChannel/productChannel/getUserProductChannel"
        },
      }
    },
    created () {
    },
    mounted:function () {
      this.channel();
    },
    methods: {
      getProduct(){
        let formData = [];
        formData.userName = form.userName;
        formData.memberType = "1";
        formData.agentUsername = "";
        if(this.agentUsername===null){
          formData.agentUsername = "";
        }
        getAction(this.url.getAllProduct,formData).then((res)=>{
          if(res.success){
            this.products = res.result;
          }else{
            this.$message.warning(res.message);
          }
        })
      },
      channel(){
        httpAction(this.url.channel,null,'get').then((res)=>{
          if(res.success){
          this.channels = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      getProductChannel:function(){
        let formData = [];
        formData.productCode = this.selected;
        formData.userName = form.userName;
        getAction(this.url.getUserProductChannel,formData).then((res)=>{
          if(res.success){
            this.channels.values = res.result.associated;
            this.channels.options = res.result.all;
          }else{
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
          this.form.setFieldsValue(pick(this.model,'userId','userName','businessCode','delFlag','createUser','updateUser'))
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
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
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